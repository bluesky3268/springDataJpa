package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"memberNo", "username", "age"}) // 연관관계가 걸려있는 필드는 toString 안하는 게 좋다(무한루프 가능성이 있음)
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long memberNo;

    private String username;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_NO")
    private Team team;

    public void changeUsername(String changeUsername) {
        this.username = changeUsername;
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

    @Builder
    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            this.team = team;
        }
    }

//    @Override
//    public String toString() {
//        return "Member{" +
//                "memberNo=" + memberNo +
//                ", username='" + username + '\'' +
//                ", age=" + age +
//                '}';
//    }
}
