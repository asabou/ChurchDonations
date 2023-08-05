package com.rurbisservices.churchdonation.dao.repository;

import com.rurbisservices.churchdonation.dao.entity.DonationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface IDonationRepository extends CrudRepository<DonationEntity, Long> {
    List<DonationEntity> findAllByChurchIdOrderByReceipt(Long churchId);

    @Query("select d from DonationEntity d where d.churchId = :churchId and (" +
            "lower(d.receipt) like lower(:filter) or " +
            "lower(d.details) like lower(:filter) or " +
            "lower(cast(d.sume as string)) like lower(:filter) " +
            ") order by d.receipt")
    List<DonationEntity> findAllByChurchIdAndFilterOrderByReceipt(Long churchId, String filter);

    @Query("select d from DonationEntity d where d.churchId = :churchId and (" +
            "(d.updateDate >= :dateFrom and d.updateDate <= :dateTo) or " +
            "(d.creationDate >= :dateFrom and d.creationDate <= :dateTo)" +
            ") order by d.sume desc, d.updateDate asc, d.creationDate asc")
    List<DonationEntity> findAllByChurchIdAndDateBetweenOrderBySumeAndDate(Long churchId, Timestamp dateFrom, Timestamp dateTo);

    @Query("select d from DonationEntity d where d.churchId = :churchId and d.personId = :personId and (" +
            "lower(d.receipt) like lower(:filter) or " +
            "lower(d.details) like lower(:filter) or " +
            "lower(cast(d.sume as string)) like lower(:filter) " +
            ") order by d.receipt")
    List<DonationEntity> findAllByChurchIdAndPersonIdAndFilterOrderByReceipt(Long churchId, Long personId, String filter);

    @Query("select d from DonationEntity d where d.churchId = :churchId and d.personId = :personId and (" +
            "(d.updateDate >= :dateFrom and d.updateDate <= :dateTo) or " +
            "(d.creationDate >= :dateFrom and d.creationDate <= :dateTo)" +
            ") order by d.sume desc, d.updateDate asc, d.creationDate asc")
    List<DonationEntity> findAllByChurchIdAndPersonIdAndDateBetweenOrderBySumeAndDate(Long churchId, Long personId, Timestamp dateFrom,
                                                                                      Timestamp dateTo);

    @Query("select d from DonationEntity d where d.churchId = :churchId and d.houseId = :houseId and (" +
            "lower(d.receipt) like lower(:filter) or " +
            "lower(d.details) like lower(:filter) or " +
            "lower(cast(d.sume as string)) like lower(:filter) " +
            ") order by d.receipt")
    List<DonationEntity> findAllByChurchIdAndHouseIdAndFilterOrderByReceipt(Long churchId, Long houseId, String filter);

    @Query("select d from DonationEntity d where d.churchId = :churchId and d.houseId = :houseId and (" +
            "(d.updateDate >= :dateFrom and d.updateDate <= :dateTo) or " +
            "(d.creationDate >= :dateFrom and d.creationDate <= :dateTo)" +
            ") order by d.sume desc, d.updateDate asc, d.creationDate asc")
    List<DonationEntity> findAllByChurchIdAndHouseIdAndDateBetweenOrderBySumeAndDate(Long churchId, Long houseId, Timestamp dateFrom, Timestamp dateTo);

    @Query("select d from DonationEntity d where d.churchId = :churchId and d.houseId = :houseId and d.personId = :personId and (" +
            "lower(d.receipt) like lower(:filter) or " +
            "lower(d.details) like lower(:filter) or " +
            "lower(cast(d.sume as string)) like lower(:filter) " +
            ") order by d.receipt")
    List<DonationEntity> findAllByChurchIdAndHouseIdAndPersonIdAndFilterOrderByReceipt(Long churchId, Long houseId, Long personId, String filter);

    @Query("select d from DonationEntity d where d.churchId = :churchId and d.houseId = :houseId and d.personId = :personId and (" +
            "(d.updateDate >= :dateFrom and d.updateDate <= :dateTo) or " +
            "(d.creationDate >= :dateFrom and d.creationDate <= :dateTo)" +
            ") order by d.sume desc, d.updateDate asc, d.creationDate asc")
    List<DonationEntity> findAllByChurchIdAndHouseIdAndPersonIdAndDateBetweenOrderBySumeAndDate(Long churchId, Long houseId, Long personId,
                                                                                                Timestamp dateFrom, Timestamp dateTo);

    List<DonationEntity> findAllByChurchIdAndHouseIdOrderByReceipt(Long churchId, Long houseId);

    List<DonationEntity> findAllByChurchIdAndHouseIdAndPersonIdOrderByReceipt(Long churchId, Long houseId, Long personId);

    @Query("select max(d.receipt) from DonationEntity d where d.churchId = :churchId and year(d.updateDate) = year(:date)")
    Long getLastReceiptByChurchIdAndCurrentYear(Long churchId, Timestamp date);

    @Query("select d from DonationEntity d where d.churchId = :churchId and d.receipt = :receipt and year(d.updateDate) = year(:updateDate)")
    List<DonationEntity> findAllByChurchIdAndReceiptAndUpdateDateYear(Long churchId, Long receipt, Timestamp updateDate);
}
