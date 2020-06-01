package jpabook.version.object;

public class Member {

    private String id;
    private String username;

    private Team team; //팀의 참조 보관

    public Member(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public void setTeam(Team team){
        this.team = team;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Team getTeam() {
        return team;
    }
}
