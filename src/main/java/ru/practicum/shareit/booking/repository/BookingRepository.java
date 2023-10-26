package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable page);

    Optional<Booking> findByIdAndItemOwnerId(long bookingId, long ownerId);

    Page<Booking> findByBooker_IdOrderByStartDesc(Long bookerId, Pageable page);

    Page<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable page);

    Page<Booking> findByBooker_IdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable page);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start desc")
    Page<Booking> findByOwner_IdOrderByStartDesc(Long ownerId, Pageable page);

    @Query("select b from Booking b where b.item.owner.id = ?1  and b.start <= ?2 and b.end >= ?3 order by b.start asc")
    Page<Booking> findByOwner_IdAndStateCurrent(Long ownerId, LocalDateTime start, LocalDateTime end, Pageable page);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end <= ?2 order by b.start desc")
    Page<Booking> findByOwner_IdAndStatePast(Long ownerId, LocalDateTime end, Pageable page);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start >= ?2 order by b.start desc")
    Page<Booking> findByOwner_IdAndStateFuture(Long ownerId, LocalDateTime start, Pageable page);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 order by b.start desc")
    Page<Booking> findByOwner_IdAndStatus(Long ownerId, BookingStatus status, Pageable page);

    List<Booking> findByItem_IdIn(Set<Long> ids);

    Boolean existsBookingByItemIdAndBookerIdAndEndIsBefore(long itemId, long userId, LocalDateTime now);

    Page<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start,
                                                                               LocalDateTime end, Pageable page);

    Optional<Booking> findFirstBookingByItem_IdAndStatusNotAndStartAfterOrderByStartAsc(long itemId, BookingStatus status,
                                                                                        LocalDateTime now);

    Optional<Booking> findFirstBookingByItem_IdAndStatusNotAndStartBeforeOrderByStartDesc(long itemId,
                                                                                          BookingStatus status,
                                                                                          LocalDateTime now);

}
