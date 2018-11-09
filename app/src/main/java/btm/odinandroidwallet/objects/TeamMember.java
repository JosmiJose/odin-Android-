package btm.odinandroidwallet.objects;

/**
 * Object of Team Member entity
 *
 */

public class TeamMember {
    public String name;
    public String linkedin;
    public String Image;

    public TeamMember(String name, String linkedin, String Image)
    {
        this.name=name;
        this.linkedin=linkedin;
        this.Image=Image;
    }
}
