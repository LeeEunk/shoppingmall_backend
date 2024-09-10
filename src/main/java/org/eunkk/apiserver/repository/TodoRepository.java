package org.eunkk.apiserver.repository;

import org.eunkk.apiserver.domain.Todo;
import org.eunkk.apiserver.repository.search.TodoSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoSearch {
}
