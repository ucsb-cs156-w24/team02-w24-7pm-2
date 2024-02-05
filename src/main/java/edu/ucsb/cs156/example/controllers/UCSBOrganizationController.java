package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
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

@Tag(name = "UCSBOrganization")
@RequestMapping("/api/ucsborganization")
@RestController
@Slf4j
public class UCSBOrganizationController extends ApiController {

    @Autowired
    UCSBOrganizationRepository ucsbOrganizationRepository;

    @Operation(summary= "List all ucsb organizations")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBOrganization> allOrganizations() {
        Iterable<UCSBOrganization> organizations = ucsbOrganizationRepository.findAll();
        return organizations;
    }

    @Operation(summary= "Get a single organization")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBOrganization getById(
            @Parameter(name="code") @RequestParam String code) {
        UCSBOrganization organizations = ucsbOrganizationRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, code));

        return organizations;
    }

    @Operation(summary= "Create a new organizations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBOrganization postorganizations(
        @Parameter(name="orgCode") @RequestParam String orgCode,
        @Parameter(name="orgTranslationShort") @RequestParam String orgTranslationShort,
        @Parameter(name="orgTranslation") @RequestParam String orgTranslation,
        @Parameter(name="inactive") @RequestParam boolean inactive
        )
        {

        UCSBOrganization organizations = new UCSBOrganization();
        organizations.setOrgCode(orgCode);
        organizations.setOrgTranslationShort(orgTranslationShort);
        organizations.setOrgTranslation(orgTranslation);
        organizations.setInactive(inactive);

        UCSBOrganization savedorganizations = ucsbOrganizationRepository.save(organizations);

        return savedorganizations;
    }

    @Operation(summary= "Update a single organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBOrganization updateOrganization(
            @Parameter(name="code") @RequestParam String code,
            @RequestBody @Valid UCSBOrganization incoming) {

        UCSBOrganization organizations = ucsbOrganizationRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, code));


        organizations.setOrgCode(incoming.getOrgCode());  
        organizations.setOrgTranslationShort(incoming.getOrgTranslationShort());
        organizations.setOrgTranslation(incoming.getOrgTranslation());
        organizations.setInactive(incoming.getInactive());

        ucsbOrganizationRepository.save(organizations);

        return organizations;
    }
}
