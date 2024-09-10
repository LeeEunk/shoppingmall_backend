package org.eunkk.apiserver.repository;

import org.eunkk.apiserver.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, String> {

    @EntityGraph(attributePaths = {"memberRoleList"}) // role + member의 email 둘다 가져와라
    @Query("select m from Member m where m.email = :email")
    Member getWithRoles(@Param("email") String email);
}
