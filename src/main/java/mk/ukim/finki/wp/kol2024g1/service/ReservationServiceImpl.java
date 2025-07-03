package mk.ukim.finki.wp.kol2024g1.service;

import mk.ukim.finki.wp.kol2024g1.model.Reservation;
import mk.ukim.finki.wp.kol2024g1.model.RoomType;
import mk.ukim.finki.wp.kol2024g1.repository.ReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static mk.ukim.finki.wp.kol2024g1.service.specifications.FieldFilterSpecification.*;

@Service
public class ReservationServiceImpl implements ReservationService{
    private final ReservationRepository reservationRepository;
    private final HotelService hotelService;

    public ReservationServiceImpl(ReservationRepository reservationRepository, HotelService hotelService) {
        this.reservationRepository = reservationRepository;
        this.hotelService = hotelService;
    }

    @Override
    public List<Reservation> listAll() {
        return this.reservationRepository.findAll();
    }

    @Override
    public Reservation findById(Long id) {
        return this.reservationRepository.findById(id).get();
    }

    @Override
    public Reservation create(String guestName, LocalDate dateCreated, Integer daysOfStay, RoomType roomType, Long hotelId) {
        return this.reservationRepository.save(new Reservation(guestName, dateCreated, daysOfStay, roomType, this.hotelService.findById(hotelId)));
    }

    @Override
    public Reservation update(Long id, String guestName, LocalDate dateCreated, Integer daysOfStay, RoomType roomType, Long hotelId) {
        Reservation reservation = this.reservationRepository.findById(id).get();
        reservation.setId(id);
        reservation.setGuestName(guestName);
        reservation.setDateCreated(dateCreated);
        reservation.setDaysOfStay(daysOfStay);
        reservation.setRoomType(roomType);
        reservation.setHotel(this.hotelService.findById(hotelId));
        return this.reservationRepository.save(reservation);
    }

    @Override
    public Reservation delete(Long id) {
        Reservation reservation = this.reservationRepository.findById(id).get();
        this.reservationRepository.delete(reservation);
        return reservation;
    }

    @Override
    public Reservation extendStay(Long id) {
        Reservation reservation = this.reservationRepository.findById(id).get();
        reservation.setDaysOfStay(reservation.getDaysOfStay() + 1);
        return this.reservationRepository.save(reservation);
    }

    @Override
    public Page<Reservation> findPage(String guestName, RoomType roomType, Long hotel, int pageNum, int pageSize) {
        Specification<Reservation> specification = Specification
                .where(filterContainsText(Reservation.class, "guestName", guestName))
                .and(filterEquals(Reservation.class, "hotel.id", hotel))
                .and(filterEqualsV(Reservation.class, "roomType", roomType));
        return this.reservationRepository.findAll(specification, PageRequest.of(pageNum, pageSize));
    }
}
