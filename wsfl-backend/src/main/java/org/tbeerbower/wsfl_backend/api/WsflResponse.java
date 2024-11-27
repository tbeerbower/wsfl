package org.tbeerbower.wsfl_backend.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WsflResponse<T> {
    private T data;
    private List<String> errors;
    private List<Link> links;
    private Meta meta;

    public WsflResponse() {
        this.links = new ArrayList<>();
    }

    public WsflResponse(T data) {
        this();
        this.data = data;
    }

    public WsflResponse(T data, List<String> errors) {
        this();
        this.data = data;
        this.errors = errors;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public void addLink(Link link) {
        this.links.add(link);
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public static class Meta {
        private long totalElements;
        private int totalPages;
        private int currentPage;
        private int pageSize;

        public Meta(long totalElements, int totalPages, int currentPage, int pageSize) {
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getPageSize() {
            return pageSize;
        }
    }
}
