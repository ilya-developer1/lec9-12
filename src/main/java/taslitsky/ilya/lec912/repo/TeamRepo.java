package taslitsky.ilya.lec912.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import taslitsky.ilya.lec912.entity.TeamEntity;

@Repository
public class TeamRepo {
  @PersistenceContext
  private EntityManager entityManager;

  public Optional<TeamEntity> findByName(String name) {
    return entityManager.createQuery("from TeamEntity where name=:name", TeamEntity.class)
        .setParameter("name", name)
        .setMaxResults(1)
        .getResultStream().findFirst();
  }
  public Long save(TeamEntity teamEntity) {
    entityManager.persist(teamEntity);
    return teamEntity.getId();
  }

  public List<TeamEntity> findAll() {
    return entityManager.createQuery("from TeamEntity", TeamEntity.class).getResultList();
  }

  public Optional<TeamEntity> findById(Long id) {
    return Optional.ofNullable(entityManager.find(TeamEntity.class, id));
  }

}
