package org.fao.geonet.api.registries.vocabularies;

import io.swagger.annotations.*;
import jeeves.server.context.ServiceContext;
import net.sf.json.JSONObject;
import org.fao.geonet.api.API;
import org.fao.geonet.api.ApiParams;
import org.fao.geonet.api.ApiUtils;
import org.fao.geonet.api.exception.ResourceNotFoundException;
import org.fao.geonet.api.registries.model.ThesaurusDto;
import org.fao.geonet.api.tools.i18n.LanguageUtils;
import org.fao.geonet.domain.ThesaurusActivation;
import org.fao.geonet.kernel.Thesaurus;
import org.fao.geonet.kernel.ThesaurusManager;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.geonet.languages.IsoLanguagesMapper;
import org.fao.geonet.repository.ThesaurusActivationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@EnableWebMvc
@Service
@RequestMapping(
    value = {
        "/api/registries/vocabularies",
        "/api/" + API.VERSION_0_1 +
            "/registries/vocabularies"
    })
@Api(
    value = ApiParams.API_CLASS_REGISTRIES_TAG,
    tags = ApiParams.API_CLASS_REGISTRIES_TAG,
    description = ApiParams.API_CLASS_REGISTRIES_OPS)
public class ThesaurusApi {
    /** The language utils. */
    @Autowired
    LanguageUtils languageUtils;

    /** The mapper. */
    @Autowired
    IsoLanguagesMapper mapper;

    /** The thesaurus manager. */
    @Autowired
    ThesaurusManager thesaurusManager;


    /**
     * Creates a thesaurus.
     *
     * @param request the request
     * @throws Exception the exception
     */
    @ApiOperation(
        value = "Retrieves thesaurus list",
        nickname = "retrieveThesaurusList",
        notes = "Retrieves thesaurus list."
    )
    @RequestMapping(
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thesaurus list.")
    })
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<ThesaurusDto> retrieveThesaurusList(
        @ApiIgnore
            HttpServletRequest request
    ) {
        ServiceContext context = ApiUtils.createServiceContext(request);

        List<ThesaurusDto> thesaurusDtoList = new ArrayList<>();
        thesaurusManager.getThesauriMap().values().forEach(
            t -> thesaurusDtoList.add(ThesaurusDto.fromThesaurus(t, context)));

        return thesaurusDtoList;
    }


    /**
     * Creates a thesaurus.
     *
     * @param thesaurusDto the thesaurus to create.
     * @param request the request
     * @throws Exception the exception
     */
    @ApiOperation(
        value = "Creates a thesaurus",
        nickname = "createThesaurus",
        notes = "Creates a thesaurus."
    )
    @RequestMapping(
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Thesaurus created."),
        @ApiResponse(code = 403, message = ApiParams.API_RESPONSE_NOT_ALLOWED_ONLY_ADMIN)
    })
    @PreAuthorize("hasRole('Administrator')")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public JSONObject createThesaurus(
        @ApiParam(
            value = "Thesaurus title.")
        @RequestBody
            ThesaurusDto thesaurusDto,
        @ApiIgnore
            HttpServletRequest request
    ) throws Exception {

        ServiceContext context = ApiUtils.createServiceContext(request);

        String filename = thesaurusDto.getFilename();
        filename = filename.trim().replaceAll("\\s+", "");

        if (!filename.endsWith(".rdf")) {
            filename = filename + ".rdf";
        }

        Path rdfFile = thesaurusManager.buildThesaurusFilePath(filename, thesaurusDto.getType(), thesaurusDto.getDname());

        final String siteURL = context.getBean(SettingManager.class).getSiteURL(context);
        final IsoLanguagesMapper isoLanguageMapper = context.getBean(IsoLanguagesMapper.class);
        Thesaurus thesaurus = new Thesaurus(isoLanguageMapper, filename,
            thesaurusDto.getTitle(), thesaurusDto.getDefaultNamespace(), thesaurusDto.getType(),
            thesaurusDto.getDname(), rdfFile, siteURL, false);
        thesaurusManager.addThesaurus(thesaurus, true);

        // Save activated status in the database
        ThesaurusActivation activation = new ThesaurusActivation();
        activation.setActivated(thesaurusDto.getActivated() == 'y'?true:false);
        activation.setId(filename);

        context.getBean(ThesaurusActivationRepository.class).save(activation);

        JSONObject response = new JSONObject();
        response.put("thesaurusKey", thesaurus.getKey());
        return response;
    }


    /**
     * Activate (enable/disable) thesaurus.
     *
     * @param thesaurus the thesaurus to enable/disable.
     * @param enable activation status.
     * @param request the request
     * @throws Exception the exception
     */
    @ApiOperation(
        value = "Enables/disables a thesaurus",
        nickname = "activateThesaurus",
        notes = "Enables/disables a thesaurus."
    )
    @RequestMapping(
        value = "/{thesaurus:.+}",
        method = RequestMethod.PUT)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thesaurus activated."),
        @ApiResponse(code = 403, message = ApiParams.API_RESPONSE_NOT_ALLOWED_ONLY_ADMIN),
        @ApiResponse(code = 404, message = ApiParams.API_RESPONSE_RESOURCE_NOT_FOUND)
    })
    @PreAuthorize("hasRole('Administrator')")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public void activateThesaurus(
        @ApiParam(
            value = "Thesaurus name.")
        @PathVariable(value = "thesaurus")
            String thesaurus,
        @ApiParam(
            value = "If set, enable the thesaurus, otherwise disable it.")
        @RequestParam
            boolean enable,
        @ApiIgnore
        HttpServletRequest request
    ) throws Exception {

        ServiceContext context = ApiUtils.createServiceContext(request);

        Thesaurus t = thesaurusManager.getThesaurusByName(thesaurus);

        if (t != null) {
            // Save activated status in the database
            final ThesaurusActivationRepository thesaurusRepository = context.getBean(ThesaurusActivationRepository.class);

            final ThesaurusActivation activation = new ThesaurusActivation();
            activation.setId(thesaurus);
            activation.setActivated(enable);

            thesaurusRepository.save(activation);
        } else {
            // Thesaurus does not exist
            throw new ResourceNotFoundException(String.format(
                "Thesaurus with identifier '%s' not found in the catalogue. Should be one of: %s",
                thesaurus,
                thesaurusManager.getThesauriMap().keySet().toString()
            ));
        }
    }

}
