package org.fao.geonet.api.registries.model;

import jeeves.server.context.ServiceContext;
import org.fao.geonet.ApplicationContextHolder;
import org.fao.geonet.domain.Constants;
import org.fao.geonet.domain.ThesaurusActivation;
import org.fao.geonet.kernel.Thesaurus;
import org.fao.geonet.repository.ThesaurusActivationRepository;

public class ThesaurusDto {
    private String key;
    private String dname;
    private String filename;
    private String title;
    private String date;
    private String url;
    private String defaultNamespace;
    private String type;
    private char activated;

    public String getKey() {
        return key;
    }

    public ThesaurusDto setKey(String key) {
        this.key = key;
        return this;
    }

    public String getDname() {
        return dname;
    }

    public ThesaurusDto setDname(String dname) {
        this.dname = dname;
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public ThesaurusDto setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ThesaurusDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDate() {
        return date;
    }

    public ThesaurusDto setDate(String date) {
        this.date = date;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ThesaurusDto setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    public ThesaurusDto setDefaultNamespace(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
        return this;
    }

    public String getType() {
        return type;
    }

    public ThesaurusDto setType(String type) {
        this.type = type;
        return this;
    }

    public char getActivated() {
        return activated;
    }

    public ThesaurusDto setActivated(char activated) {
        this.activated = activated;
        return this;
    }


    public static ThesaurusDto fromThesaurus(Thesaurus thesaurus, ServiceContext context) {
        ThesaurusDto thesaurusDto = new ThesaurusDto()
            .setKey(thesaurus.getKey())
            .setDate(thesaurus.getDate())
            .setDefaultNamespace(thesaurus.getDefaultNamespace())
            .setFilename(thesaurus.getFname())
            .setDname(thesaurus.getDname())
            .setUrl(thesaurus.getDownloadUrl())
            .setType(thesaurus.getType());

        String title;

        try {
            title = thesaurus.getTitles(context.getApplicationContext()).get(context.getLanguage());

            if (title == null) {
                title = thesaurus.getTitle();
            }
        } catch (Exception ex) {
            title = thesaurus.getTitle();
        }

        thesaurusDto.setTitle(title);

        char activated = Constants.YN_TRUE;
        final ThesaurusActivationRepository activationRepository = context.getBean(ThesaurusActivationRepository.class);
        final ThesaurusActivation activation = activationRepository.findOne(thesaurus.getKey());
        if (activation != null && !activation.isActivated()) {
            activated = Constants.YN_FALSE;
        }

        thesaurusDto.setActivated(activated);

        return thesaurusDto;
    }
}
