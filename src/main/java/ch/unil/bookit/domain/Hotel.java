package ch.unil.bookit.domain;
import com.bookit.domain.user.HotelManager;
import jakarta.persistence.*;
import java.util.*;

public class Hotel {

    private UUID id;
    private HotelManager manager;

    private String name;
    private String city;
    private String country;
    private String description;
    private String baseCurrency = "USD";
    private List<RoomType> roomTypes = new ArrayList<>();
    public void addRoomType(RoomType rt) {
        rt.setHotel(this);
        roomTypes.add(rt);
    }
}

