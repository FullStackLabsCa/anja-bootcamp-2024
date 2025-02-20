package io.reactivestax.active.life.canada.service;

import io.reactivestax.active.life.canada.repository.FamilyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final FamilyMemberRepository familyMemberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return familyMemberRepository.findByMemberLoginId(username)
                .stream().map(familyMember -> User.withUsername(familyMember.getMemberLoginId())
                        .password(familyMember.getFamilyGroup().getFamilyPin()).build())
                .findFirst().orElse(null);
    }
}
