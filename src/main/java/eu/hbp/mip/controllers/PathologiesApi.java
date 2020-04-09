/**
 * Created by mirco on 04.12.15.
 */

package eu.hbp.mip.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eu.hbp.mip.model.PathologyDTO;
import eu.hbp.mip.model.PathologyDTO.PathologyDatasetDTO;
import eu.hbp.mip.model.UserInfo;
import eu.hbp.mip.utils.CustomResourceLoader;
import eu.hbp.mip.utils.UserActionLogging;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/pathologies", produces = {APPLICATION_JSON_VALUE})
@Api(value = "/pathologies")
public class PathologiesApi {

    private static final Gson gson = new Gson();

    @Autowired
    private UserInfo userInfo;

    @Autowired
    private CustomResourceLoader resourceLoader;

    @RequestMapping(name = "/pathologies", method = RequestMethod.GET)
    public ResponseEntity<String> getPathologies(Authentication authentication) {
        UserActionLogging.LogUserAction(userInfo.getUser().getUsername(), "Load all the pathologies", "");

        Resource resource = resourceLoader.getResource("file:/opt/portal/api/pathologies.json");
        List<PathologyDTO> allPathologies;
        try {
            allPathologies = gson.fromJson(convertInputStreamToString(resource.getInputStream()), new TypeToken<List<PathologyDTO>>() {
            }.getType());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("{\"error\" : \"The pathologies.json file could not be read.\"}");
        }

        // --- Providing only the allowed pathologies/datasets to the user  ---
        UserActionLogging.LogUserAction(userInfo.getUser().getUsername(),
                "Load all the pathologies", "Filter out the unauthorised datasets.");

        List<String> userRoles = Arrays.asList(authentication.getAuthorities().toString().toLowerCase()
                .replaceAll("[\\s+\\]\\[]","").split(","));

        UserActionLogging.LogUserAction(userInfo.getUser().getUsername(),
                "Load all the pathologies", "Authorities : " + authentication.getAuthorities().toString());

        UserActionLogging.LogUserAction(userInfo.getUser().getUsername(),
                "Load all the pathologies", "Authorities: " + userRoles);

        List<PathologyDTO> userPathologies = new ArrayList<>();
        for (PathologyDTO curPathology : allPathologies) {
            UserActionLogging.LogUserAction(userInfo.getUser().getUsername(),
                    "Load all the pathologies", "Pathology: " + curPathology);

            List<PathologyDatasetDTO> userPathologyDatasets = new ArrayList<PathologyDatasetDTO>();
            for (PathologyDatasetDTO dataset : curPathology.getDatasets()) {
                if(userRoles.contains("role_" + dataset.getCode())){
                    userPathologyDatasets.add(dataset);
                }
            }

            UserActionLogging.LogUserAction(userInfo.getUser().getUsername(),
                    "Load all the pathologies", "User Pathologies size: " + userPathologyDatasets.size());

            if(userPathologyDatasets.size() > 0){
                UserActionLogging.LogUserAction(userInfo.getUser().getUsername(),
                        "Load all the pathologies", "Added the pathology");

                PathologyDTO userPathology = new PathologyDTO();
                userPathology.setCode(curPathology.getCode());
                userPathology.setLabel(curPathology.getLabel());
                userPathology.setMetadataHierarchy(curPathology.getMetadataHierarchy());
                userPathology.setDatasets(userPathologyDatasets);
                userPathologies.add(userPathology);
            }
        }

        return ResponseEntity.ok().body(gson.toJson(userPathologies));
    }

    // Pure Java
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString(StandardCharsets.UTF_8.name());

    }
}
