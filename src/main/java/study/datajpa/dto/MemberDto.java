package study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import study.datajpa.entity.Member;

@Getter
@Setter
//@AllArgsConstructor
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    @Builder
    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    /**
     * dto는 entity를 바라봐도 괜찮지만
     * entity는 dto를 바라보지 않는 것이 좋다.
     */
    public MemberDto(Member member) {
        this.id = member.getMemberNo();
        this.username = member.getUsername();
        this.teamName = member.getTeam() == null ? "" : member.getTeam().getName();
    }

    @Override
    public String toString() {
        return "MemberDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", teamName='" + teamName + '\'' +
                '}';
    }
}
