package dev.dogeared.ctfdaccounthook.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CtfdMeta {

    private CtfdPagination pagination;

    public CtfdPagination getPagination() {
        return pagination;
    }

    public void setPagination(CtfdPagination pagination) {
        this.pagination = pagination;
    }

    public static class CtfdPagination {

        private Integer next;
        private Integer page;
        private Integer pages;

        @JsonProperty("per_page")
        private Integer perPage;

        private Integer prev;
        private Integer total;

        public Integer getNext() {
            return next;
        }

        public void setNext(Integer next) {
            this.next = next;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getPages() {
            return pages;
        }

        public void setPages(Integer pages) {
            this.pages = pages;
        }

        public Integer getPerPage() {
            return perPage;
        }

        public void setPerPage(Integer perPage) {
            this.perPage = perPage;
        }

        public Integer getPrev() {
            return prev;
        }

        public void setPrev(Integer prev) {
            this.prev = prev;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }
    }
}
