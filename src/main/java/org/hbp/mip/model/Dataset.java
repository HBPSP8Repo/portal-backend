/**
 * Created by mirco on 04.12.15.
 */

package org.hbp.mip.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "`dataset`")
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dataset {

    @Id
    private String code = null;

    private Date date = null;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "dataset_header", joinColumns = @JoinColumn(name = "dataset_code"))
    private List<String> header = new LinkedList<>();

    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name = "dataset_data", joinColumns = @JoinColumn(name = "dataset_code"))
    private Map<String, LinkedList<Object>> data = new HashMap<>();


    public Dataset() {
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }


    public Map<String, LinkedList<Object>> getData() {
        return data;
    }

    public void setData(Map<String, LinkedList<Object>> data) {
        this.data = data;
    }

}
