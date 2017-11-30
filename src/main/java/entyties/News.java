package entyties;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "news")
public class News {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "datetime")
    private LocalDateTime dateTime;
    @Column(name = "header")
    private String header;
    @Lob
    @Column(name = "body1",columnDefinition = "TEXT")
    private String body1;
    @Column(name = "body2",columnDefinition = "TEXT")
    private String body2;
    @ElementCollection
    private List<String> imageNames;
    @Column(name = "countimages")
    private int countimages;
    @Column(name = "imagesPlace")
    private String imagesPlace; //left,top,bottom,center
    @Column(name = "youtube")
    private String youtube;
    @Column(name = "youtubePlace")
    private String youtubePlace;


    public News() {
    }

    public News(LocalDateTime dateTime, String header, String body1, String body2, List<String> imageNames) {
        this.dateTime = dateTime;
        this.header = header;
        this.body1 = body1;
        this.body2 = body2;
        this.imageNames = imageNames;
        this.countimages = imageNames!=null?imageNames.size():0;
    }

    public String getId() {
        return id.toString();
    }
    public String getDateTime() {
        return dateTime.toLocalDate().toString();
    }

    public String getHeader() {
        return header;
    }

    public String getBody1() {
        return body1;
    }

    public String getBody2() {
        return body2;
    }

    public List<String> getImageNames() {
        return imageNames;
    }

    public void setImageNames(List<String> imageNames) {
        this.imageNames = imageNames;
    }

    public int getCountimages() {
        return countimages;
    }

    public void setCountimages(int countimages) {
        this.countimages = countimages;
    }

    public String getImagesPlace() {
        return imagesPlace;
    }

    public void setImagesPlace(String imagesPlace) {
        this.imagesPlace = imagesPlace;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getYoutubePlace() {
        return youtubePlace;
    }

    public void setYoutubePlace(String youtubePlace) {
        this.youtubePlace = youtubePlace;
    }

    @Override
    public String toString() {
        return "\nid: "+id
                +"\nheader: "+header
                +"\ndatetime: "+dateTime
                +"\ncountimages="+countimages;
    }
}
