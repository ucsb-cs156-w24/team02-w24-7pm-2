package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.time.LocalDateTime;

@Tag(name = "UCSBDiningCommonsMenuItem")
@RequestMapping("/api/UCSBDiningCommonsMenuItem")
@RestController
@Slf4j
public class UCSBDiningCommonsMenuItemController extends ApiController {

    @Autowired
    UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;

    @Operation(summary= "List all ucsb dining commons menu items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBDiningCommonsMenuItem> allUCSBDiningCommonsMenuItems() {
        Iterable<UCSBDiningCommonsMenuItem> items = ucsbDiningCommonsMenuItemRepository.findAll();
        return items;
    }

    @Operation(summary= "Create a new dining common menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDiningCommonsMenuItem postUCSBDiningCommonsMenuItem(
            @Parameter(name="diningCommonsCode") @RequestParam String diningCommonsCode,
            @Parameter(name="name") @RequestParam String name,
            @Parameter(name="station") @RequestParam String station)
            throws JsonProcessingException {

        UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItem = new UCSBDiningCommonsMenuItem();
        ucsbDiningCommonsMenuItem.setDiningCommonsCode(diningCommonsCode);
        ucsbDiningCommonsMenuItem.setName(name);
        ucsbDiningCommonsMenuItem.setStation(station);

        UCSBDiningCommonsMenuItem savedUcsbDiningCommonsMenuItem = ucsbDiningCommonsMenuItemRepository.save(ucsbDiningCommonsMenuItem);

        return savedUcsbDiningCommonsMenuItem;
    }

    // @Operation(summary= "Get a single date")
    // @PreAuthorize("hasRole('ROLE_USER')")
    // @GetMapping("")
    // public UCSBDate getById(
    //         @Parameter(name="id") @RequestParam Long id) {
    //     UCSBDate ucsbDate = ucsbDateRepository.findById(id)
    //             .orElseThrow(() -> new EntityNotFoundException(UCSBDate.class, id));

    //     return ucsbDate;
    // }

    // @Operation(summary= "Delete a UCSBDate")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @DeleteMapping("")
    // public Object deleteUCSBDate(
    //         @Parameter(name="id") @RequestParam Long id) {
    //     UCSBDate ucsbDate = ucsbDateRepository.findById(id)
    //             .orElseThrow(() -> new EntityNotFoundException(UCSBDate.class, id));

    //     ucsbDateRepository.delete(ucsbDate);
    //     return genericMessage("UCSBDate with id %s deleted".formatted(id));
    // }

    // @Operation(summary= "Update a single date")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @PutMapping("")
    // public UCSBDate updateUCSBDate(
    //         @Parameter(name="id") @RequestParam Long id,
    //         @RequestBody @Valid UCSBDate incoming) {

    //     UCSBDate ucsbDate = ucsbDateRepository.findById(id)
    //             .orElseThrow(() -> new EntityNotFoundException(UCSBDate.class, id));

    //     ucsbDate.setQuarterYYYYQ(incoming.getQuarterYYYYQ());
    //     ucsbDate.setName(incoming.getName());
    //     ucsbDate.setLocalDateTime(incoming.getLocalDateTime());

    //     ucsbDateRepository.save(ucsbDate);

    //     return ucsbDate;
    // }
}
