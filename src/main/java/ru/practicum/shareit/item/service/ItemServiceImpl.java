package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IllegalOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, ObjectMapper objectMapper, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    @Override
    public ItemDto postItem(long userId, ItemDto itemDto) {
        log.info("Posting item {} by user with id {}", itemDto, userId);

        Item itemToPost = ItemMapper.toItem(itemDto);
        itemToPost.setOwner(userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " is not found")));
        return ItemMapper.toItemDto(itemRepository.save(itemToPost));
    }

    @Transactional
    @Override
    public CommentDto postComment(long userId, long itemId, CommentDto commentDto) {
        log.info("Posting comment {} by user with id {} to item with id {}", commentDto, userId, itemId);

        if (!bookingRepository.existsBookingByItemIdAndBookerIdAndEndIsBefore(itemId, userId, LocalDateTime.now())) {
            log.debug("User with id {} haven't booked the item with id {} yet", userId, itemId);
            throw new IllegalOperationException("Only user who earlier booked item can leave a comment");
        }
        Comment commentToPost = CommentMapper.toComment(commentDto);
        commentToPost.setAuthor(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " +
                "with id " + userId + " is not found")));
        commentToPost.setItem(itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item with id "
                + itemId + " is not found.")));

        return CommentMapper.toCommentDto(commentRepository.save(commentToPost));
    }

    @Transactional
    @Override
    public ItemDto partiallyUpdateItem(long userId, long itemId, ItemDto itemDto) throws JsonMappingException {
        log.info("Updating item with id {} by user with id {} to {}", itemId, userId, itemDto);
        Item itemToUpdate = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Item not found");
        });
        if (itemToUpdate.getOwner().getId() != userId) {
            throw new NotFoundException("Item with id " + itemId + " for user with id " + userId + " not found");
        }
        objectMapper.updateValue(itemToUpdate, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(itemToUpdate));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemWithBookingsAndCommentsDto getItemByIdAnyUser(long userId, long itemId) {
        log.info("Retrieving item with id {} by user with id {}", itemId, userId);
        Item searchedItem = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Item with id " + itemId + " is not found");
        });
        Optional<Booking> last = Optional.empty();
        Optional<Booking> next = Optional.empty();

        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        if (userId == searchedItem.getOwner().getId()) {
            last = bookingRepository.findFirstBookingByItem_IdAndStatusNotAndStartBeforeOrderByStartDesc(itemId,
                    BookingStatus.REJECTED, LocalDateTime.now());
            next = bookingRepository.findFirstBookingByItem_IdAndStatusNotAndStartAfterOrderByStartAsc(itemId,
                    BookingStatus.REJECTED, LocalDateTime.now());
        }
        return ItemMapper.toItemWithBookingsAndCommentsDto(searchedItem, last, next,
                CommentMapper.toCommentDtoList(comments));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemWithBookingsAndCommentsDto> getAllItemsOwner(long userId) {
        log.info("Retrieving all items by owner with id {}", userId);
        Map<Long, Item> itemMap = itemRepository.findItemByOwnerId(userId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        Map<Long, List<Booking>> bookingsMap = bookingRepository.findByItem_IdIn(itemMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        Map<Long, List<Comment>> commentsMap = commentRepository.findAllByItemIdIn(itemMap.keySet()).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return itemMap.values()
                .stream()
                .map(item -> ItemMapper.toItemWithBookingsAndCommentsDto(item,
                        getLastBooking(bookingsMap.getOrDefault(item.getId(), Collections.emptyList())),
                        getNextBooking(bookingsMap.getOrDefault(item.getId(), Collections.emptyList())),
                        CommentMapper.toCommentDtoList(commentsMap.getOrDefault(item.getId(), Collections.emptyList()))))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItem(long userId, String text) {
        log.info("Searching item which contains {}", text);
        if (text == null || text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Optional<Booking> getLastBooking(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            return Optional.empty();
        }
        return bookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                        && !booking.getStatus().equals(BookingStatus.REJECTED))
                .max(Comparator.comparing(Booking::getStart));
    }

    private Optional<Booking> getNextBooking(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            return Optional.empty();
        }
        Comparator<Booking> comparator = Comparator.comparing(Booking::getStart);
        return bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())
                        && !booking.getStatus().equals(BookingStatus.REJECTED))
                .min(comparator);
    }

}