package org.eunkk.apiserver.repository.search;

import org.eunkk.apiserver.domain.Todo;
import org.eunkk.apiserver.dto.PageRequestDTO;
import org.springframework.data.domain.Page;

public interface TodoSearch {

    Page<Todo> search1(PageRequestDTO pageRequestDTO);
}
