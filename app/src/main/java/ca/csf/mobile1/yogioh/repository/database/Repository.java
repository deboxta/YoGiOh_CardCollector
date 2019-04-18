package ca.csf.mobile1.yogioh.repository.database;

import java.util.List;

public interface Repository<T> {

    List<T> findAll()throws RepositoryException;

    void create(T item)throws RepositoryException;

    void update(T item)throws RepositoryException;

    void delete(T item)throws RepositoryException;

}
