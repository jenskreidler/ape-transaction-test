package de.jodes.report.ape;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@LocalBean
public class UserService {

    @PersistenceContext(unitName = "ape-pu")
    private EntityManager em;

    public User saveUser(User u) {
        em.persist(u);
        return u;
    }

    public User getUser(Long id) {
        return em.createQuery("from User where id = ?1", User.class).setParameter(1, id).getSingleResult();
    }
}
