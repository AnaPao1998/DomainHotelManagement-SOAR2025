package ch.unil.bookit.bookitwebapp.ui;

import ch.unil.bookit.bookitwebapp.BookItService;
import ch.unil.bookit.domain.Hotel;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.primefaces.PrimeFaces;

@SessionScoped
@Named
public class HotelBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    BookItService service;

    @Inject
    ManagerBean managerBean; // Inject the ManagerBean to get the ID of the logged-in manager

    private List<Hotel> hotels;
    private List<Hotel> allHotels;
    private Hotel selectedHotel;
    private boolean isCreatingNewHotel;

    @PostConstruct
    public void init() {
        loadAllHotels(); // Initial load
        selectedHotel = new Hotel();
    }


    // Loads all hotels associated with the currently logged-in manager. -> for HotelManagement.xhtml
    public void loadAllHotels() {
        UUID managerId = managerBean.getManagerId();
        System.out.println("Manager ID: " + managerId);
        if (managerId != null) {
            try {
                // Use the new service method to fetch filtered hotels
                this.allHotels = service.getHotelsByManager(managerId);
                if (this.allHotels == null) {
                    this.allHotels = Collections.emptyList();
                }
                System.out.println("DEBUG HotelBean: Number of hotels received: " + this.allHotels.size());
            } catch (Exception e) {
                System.err.println("ERROR HotelBean: Failed to fetch hotels: " + e.getMessage());
                e.printStackTrace();
                this.allHotels = Collections.emptyList();
            }
        } else {
            System.out.println("Manager ID is NULL, hotels list will be empty.");
            this.allHotels = Collections.emptyList();
        }
    }

    // CRUD Action Helpers

    public void initNewHotel() {
        this.selectedHotel = new Hotel();
        this.isCreatingNewHotel = true;
    }

    public void saveHotel() {
        if (isCreatingNewHotel) {
            createHotel();
        } else {
            updateHotel();
        }
        loadAllHotels();
    }

    private void createHotel() {
        try {
            UUID managerId = managerBean.getManagerId();

            if (managerId == null) {
                throw new Exception("You must be logged in as a manager to create a hotel.");
            }

            selectedHotel.setManagerId(managerId);

            service.createHotel(selectedHotel);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Hotel '" + selectedHotel.getName() + "' created successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to create hotel: " + e.getMessage()));
        }
    }

    private void updateHotel() {
        try {
            service.updateHotelAndReturn(selectedHotel);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Hotel '" + selectedHotel.getName() + "' updated successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update hotel: " + e.getMessage()));
        }
    }

    public void deleteHotel(UUID hotelId) {
        try {
            service.deleteHotel(hotelId);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Hotel deleted successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete hotel: " + e.getMessage()));
        }
        loadAllHotels(); // Reload the list
    }


    // Getters and Setters

    public List<Hotel> getAllHotels() {
        return allHotels;
    }

    public Hotel getSelectedHotel() {
        return selectedHotel;
    }

    public void setSelectedHotel(Hotel hotel) {
        this.selectedHotel = hotel;
        this.isCreatingNewHotel = false;
    }

    public boolean getIsCreatingNewHotel() {
        return isCreatingNewHotel;
    }
}