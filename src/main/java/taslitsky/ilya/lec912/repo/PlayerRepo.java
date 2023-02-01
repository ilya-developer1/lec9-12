package taslitsky.ilya.lec912.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import taslitsky.ilya.lec912.entity.PlayerEntity;

@Repository
@AllArgsConstructor
public class PlayerRepo {

  @PersistenceContext
  private EntityManager entityManager;

  public Optional<PlayerEntity> findById(Long id) {
    return Optional.ofNullable(entityManager.find(PlayerEntity.class, id));
  }

  public List<PlayerEntity> findAll() {
    return entityManager.createQuery("from PlayerEntity", PlayerEntity.class).getResultList();
  }

  public List<PlayerEntity> findBySurname(String surname, int start, int finish) {
    return entityManager.createQuery("from PlayerEntity where surname=:surname", PlayerEntity.class)
        .setParameter("surname", surname)
        .setFirstResult(start-1)
        .setMaxResults(finish-start+1)
        .getResultList();
  }

  public List<PlayerEntity> findByName(String name, int start, int finish) {
    return entityManager.createQuery("from PlayerEntity where name=:name", PlayerEntity.class)
        .setParameter("name", name)
        .setFirstResult(start-1)
        .setMaxResults(finish-start+1)
        .getResultList();
  }

  public void deleteAll() {
    List<PlayerEntity> playerEntities = findAll();
    List<Long> idList = getIdsFromEntity(playerEntities);
    entityManager.createQuery("delete PlayerEntity p where id in (:ids )")
        .setParameter("ids", idList)
        .executeUpdate();
  }

  public Long save(PlayerEntity playerEntity) {
    entityManager.persist(playerEntity);
    return playerEntity.getId();
  }

  public PlayerEntity update(PlayerEntity playerEntity) {
    return entityManager.merge(playerEntity);
  }

  public void delete(PlayerEntity playerEntity) {
    entityManager.remove(playerEntity);
  }

  private List<Long> getIdsFromEntity(List<PlayerEntity> playerEntityList) {
      List<Long> idList = new ArrayList<>();
      playerEntityList.forEach(playerEntity -> idList.add(playerEntity.getId()));
      return idList;
  }

}
