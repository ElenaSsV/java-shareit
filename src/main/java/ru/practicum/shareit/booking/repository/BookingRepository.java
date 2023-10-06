package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    Optional<Booking> findByIdAndItemOwnerId(long bookingId, long ownerId);
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByBooker_IdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start desc")
    List<Booking> findByOwner_IdOrderByStartDesc(Long ownerId);

    @Query("select b from Booking b where b.item.owner.id = ?1  and b.start <= ?2 and b.end >= ?3 order by b.start asc")
    List<Booking> findByOwner_IdAndStateCurrent(Long ownerId, LocalDateTime start, LocalDateTime end);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end <= ?2 order by b.start desc")
    List<Booking> findByOwner_IdAndStatePast(Long ownerId, LocalDateTime end);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start >= ?2 order by b.start desc")
    List<Booking> findByOwner_IdAndStateFuture(Long ownerId, LocalDateTime start);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findByOwner_IdAndStatus(Long ownerId, BookingStatus status);

    List<Booking> findByItem_IdIn(Set<Long> ids);

    Boolean existsBookingByItemIdAndBookerIdAndEndIsBefore(long itemId, long userId, LocalDateTime now);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

}
