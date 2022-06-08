package antifraud;

import antifraud.models.Access;
import antifraud.models.Role;
import antifraud.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.util.List;

@Service
public class JpaRepository {
    @Autowired
    EntityManagerFactory entityManagerFactory;

    public long addUser(User user) {
        if (getUser(user.getUsername()) != null) {
            return 0;
        }
        Role role = getRole();
        user.setRole(role);
        if (role == Role.MERCHANT) {
            user.setEnabled(false);
        }

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        // entityManager.flush();
        entityManager.getTransaction().commit();
        entityManager.close();
        return user.getUserId();

    }

    public Role getRole() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        long num = entityManager.createQuery("SELECT COUNT(*) from user", Long.class)
                .getSingleResult();
        entityManager.close();
        if (num == 0) {
            return Role.ADMINISTRATOR;
        }
        return Role.MERCHANT;
    }


    public User getUser(String username) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            User user = entityManager.createQuery("SELECT u FROM user u WHERE LOWER(u.username) = :value1", User.class)
                    .setParameter("value1", username.toLowerCase())
                    .getSingleResult();
            entityManager.close();
            return user;
        } catch (NoResultException ex) {
            entityManager.close();
            return null;
        }
    }

    public List<User> findAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<User> list = entityManager.createQuery("SELECT u FROM user u", User.class)
                .getResultList();
        entityManager.close();
        return list;

    }

    public boolean deleteUser(String username) {

        User user = getUser(username);
        if (user == null) {
            return false;
        }
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
        entityManager.getTransaction().commit();
        entityManager.close();
        return true;
    /*    long num = entityManager.createQuery("DELETE FROM user WHERE LOWER(username) = :value1", Long.class)
                .setParameter("value1", username.toLowerCase()).getSingleResult();
        entityManager.flush();
        if (num > 0) {
            return true;
        }
        return false;*/
    }

    public User changeRole(User usr) {
        // User usr = getUser(user.getUsername());
        //if (usr == null ||) {
        //      return null;
        //   }
        // usr.setRole(user.getRole());
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(usr);
        entityManager.getTransaction().commit();
        entityManager.close();
        return usr;
    }

    public boolean access(Access access) {
        User usr = getUser(access.getUsername());
        if (usr != null && usr.getRole() != Role.ADMINISTRATOR) {
            usr.setEnabled(access.getOperation() != Access.AccessEnum.LOCK);
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.merge(usr);
            entityManager.getTransaction().commit();
            entityManager.close();
            return true;
        }
        return false;
    }

}
