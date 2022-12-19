package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public Member findMember(@PathVariable("id") Long id) throws Exception{
        return memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("유저 없음"));
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) throws Exception{
        return member.getUsername();
    }

    //http://localhost:8080/members?page=2&size=20&sort=memberNo,desc
    @GetMapping("/members")
    public Page<MemberDto> findMembers(@PageableDefault(size = 5) Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberDto::new);
    }

//    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(Member.builder()
                    .username("member" + i)
                    .age(i)
                    .build());
        }
    }
}
