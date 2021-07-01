package eu.hbp.mip.models.DTOs;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

@Getter
@Setter
public class MIPEngineAlgorithmDTO {

    @SerializedName("name")
    private String name;

    @SerializedName("desc")
    private String desc;

    @SerializedName("label")
    private String label;

    @SerializedName("type")
    private String type;

    @SerializedName("parameters")
    private InputDataParamDTO parameters;

    @SerializedName("crossvalidation")
    private String crossvalidation;

    @SerializedName("inputdata")
    private Hashtable<String, InputDataParamDTO> inputdata;

    @Getter
    @Setter
    public static class InputDataParamDTO {

        @SerializedName("stattypes")
        private List<String> stattypes;

        @SerializedName("label")
        private String label;

        @SerializedName("notblank")
        private String notblank;

        @SerializedName("enumslen")
        private Integer enumslen;

        @SerializedName("multiple")
        private String multiple;

        @SerializedName("types")
        private List<String> types;

        @SerializedName("desc")
        private String desc;

        public AlgorithmDTO.AlgorithmParamDTO convertToAlgorithmParamDTO(String name) throws Exception {
            AlgorithmDTO.AlgorithmParamDTO algorithmParamDTO = new AlgorithmDTO.AlgorithmParamDTO();
            algorithmParamDTO.setName(name);
            algorithmParamDTO.setDesc(this.desc);
            if(name.equals("datasets") || name.equals("filters") || name.equals("pathology")){
                algorithmParamDTO.setValueType(this.types.get(0));
                if(name.equals("filters")){
                    algorithmParamDTO.setName("filter");
                    algorithmParamDTO.setType("filter");
                }
                if(name.equals("datasets")){
                    algorithmParamDTO.setName("dataset");
                    algorithmParamDTO.setType("dataset");
                }
            }
            else{
                algorithmParamDTO.setType("column");
                algorithmParamDTO.setColumnValuesSQLType(String.join(", ", this.types));
                algorithmParamDTO.setColumnValuesIsCategorical(getColumnValuesIsCategorical(this.stattypes));
            }
            algorithmParamDTO.setValue("");
            algorithmParamDTO.setValueNotBlank(this.notblank);
            algorithmParamDTO.setValueMultiple(this.multiple);
            String[] hidden = {"x","y","dataset", "filter","pathology","centers","formula"};
            algorithmParamDTO.setLabel(Arrays.asList(hidden).contains(algorithmParamDTO.getName()) ? algorithmParamDTO.getName():this.label);

            return algorithmParamDTO;
        }
        private String getColumnValuesIsCategorical(List<String> stattypes) throws Exception {

            if (stattypes.contains("nominal") && stattypes.contains("numerical")){
                return "";
            }
            else if (stattypes.contains("nominal")){
                return "true";
            }
            else if (stattypes.contains("numerical")){
                return "false";
            }
            else{
                throw new Exception("Invalid stattypes");
            }
        }
    }

    public AlgorithmDTO convertToAlgorithmDTO()
    {
        AlgorithmDTO algorithmDTO = new AlgorithmDTO();
        algorithmDTO.setName(this.name.toUpperCase());
        algorithmDTO.setLabel(this.label);
        algorithmDTO.setDesc(this.desc);
        algorithmDTO.setType("mipengine");
        List<AlgorithmDTO.AlgorithmParamDTO> parameters = new ArrayList<>();
        this.inputdata.forEach((name, inputDataParamDTO) -> {
            try {
                AlgorithmDTO.AlgorithmParamDTO parameter = inputDataParamDTO.convertToAlgorithmParamDTO(name);
                parameters.add(parameter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        algorithmDTO.setParameters(parameters);
        return algorithmDTO;
    }
}
