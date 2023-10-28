package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.RequestBooking;
import ru.practicum.shareit.booking.dto.ResponseBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IllegalOperationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public ResponseBooking postBooking(long bookerId, RequestBooking bookingDto) {
        Item itemToBook = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Item with id " + bookingDto.getItemId() + " is not found"));
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("User is not found"));

        if (itemToBook.getOwner().getId() == bookerId) {
            throw new NotFoundException("Owner cannot book item");
        }

        if (!itemToBook.getAvailable() || bookingDto.getEnd().isBefore(bookingDto.getStart())
                || bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new IllegalOperationException("Operation is not allowed");
        }
        Booking bookingToPost = BookingMapper.toBooking(bookingDto);

        bookingToPost.setBooker(booker);
        bookingToPost.setItem(itemToBook);

        log.info("Saving booking {}", bookingToPost);
        return BookingMapper.toResponseBooking(bookingRepository.save(bookingToPost));
    }

    @Transactional
    @Override
    public ResponseBooking updateBookingStatus(long userId, boolean isApproved, long bookingId) {
        log.info("Updating booking id {} status by user {} to isApproved {}", bookingId, userId, isApproved);

        Booking bookingToUpdate = bookingRepository.findByIdAndItemOwnerId(bookingId, userId).orElseThrow(() ->
                new NotFoundException("Booking for item with owner id " +  userId + " is not found"));

        if (bookingToUpdate.getStatus().equals(BookingStatus.APPROVED) && isApproved) {
            throw new IllegalOperationException("Booking is already approved");
        }
        if (bookingToUpdate.getStatus().equals(BookingStatus.REJECTED) && !isApproved) {
            throw new IllegalOperationException("Booking is already rejected");
        }

        if (isApproved) {
            bookingToUpdate.setStatus(BookingStatus.APPROVED);
        } else {
            bookingToUpdate.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toResponseBooking(bookingRepository.save(bookingToUpdate));
    }

    @Override
    public ResponseBooking getBookingById(long userId, long bookingId) {
        log.info("Retrieving booking with id {} by user with id {}", bookingId, userId);

        Booking searchedBooking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking with id " + bookingId + " is not found."));
        if (userId != searchedBooking.getBooker().getId() && userId != searchedBooking.getItem().getOwner().getId()) {
            throw new NotFoundException("Booking is not found for user with id " + userId + ".");
        }
        return BookingMapper.toResponseBooking(searchedBooking);
    }

    @Override
    public List<ResponseBooking> getAllBookingsByBooker(BookingState bookingState, long userId, int from, int size) {
        log.info("Retrieving bookings by user with id {} and state {}", userId, bookingState);

        PageRequest page = PageRequest.of(from / size, size);

        if (!userRepository.existsUserById(userId)) {
            throw new NotFoundException("User with id " + userId + " is not found");
        }

        switch (bookingState) {
            case PAST:
                return BookingMapper.toResponseBookingList(bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(), page));
            case CURRENT:
                return BookingMapper.toResponseBookingList(bookingRepository
                        .findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(),
                                LocalDateTime.now(), page));
            case FUTURE:
                return BookingMapper.toResponseBookingList(bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), page));
            case WAITING:
                return BookingMapper.toResponseBookingList(bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId,
                        BookingStatus.WAITING, page));
            case REJECTED:
                return BookingMapper.toResponseBookingList(bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId,
                        BookingStatus.REJECTED, page));
            default:
                return BookingMapper.toResponseBookingList(bookingRepository.findByBooker_IdOrderByStartDesc(userId, page));
        }
    }

    @Override
    public List<ResponseBooking> getAllBookingsByItemOwner(BookingState bookingState, long userId, int from, int size) {
        log.info("Retrieving bookings by user with id {} and state {}", userId, bookingState);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        if (!userRepository.existsUserById(userId)) {
            throw new NotFoundException("User with id " + userId + " is not found");
        }

        switch (bookingState) {
            case PAST:
                return BookingMapper.toResponseBookingList(bookingRepository.findByOwner_IdAndStatePast(userId,
                        LocalDateTime.now(), page));
            case CURRENT:
                return BookingMapper.toResponseBookingList(bookingRepository.findByOwner_IdAndStateCurrent(userId,
                        LocalDateTime.now(), LocalDateTime.now(), page));
            case FUTURE:
                return BookingMapper.toResponseBookingList(bookingRepository.findByOwner_IdAndStateFuture(userId,
                        LocalDateTime.now(), page));
            case WAITING:
                return BookingMapper.toResponseBookingList(bookingRepository.findByOwner_IdAndStatus(userId,
                        BookingStatus.WAITING, page));
            case REJECTED:
                return BookingMapper.toResponseBookingList(bookingRepository.findByOwner_IdAndStatus(userId,
                        BookingStatus.REJECTED, page));
            default:
                return BookingMapper.toResponseBookingList(bookingRepository.findByOwner_IdOrderByStartDesc(userId, page));
        }
    }
}
