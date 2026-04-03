package mx.edu.utez.fixmycity_backend.dto.response;

import java.util.List;

/**
 * Respuesta paginada para feed y mis reportes cuando se envían query params page y size.
 */
public class ReportePageResponse {

    private List<ReporteResponse> content;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
    private boolean last;

    public ReportePageResponse() {
    }

    public ReportePageResponse(List<ReporteResponse> content, long totalElements, int totalPages,
                               int page, int size, boolean last) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.page = page;
        this.size = size;
        this.last = last;
    }

    public List<ReporteResponse> getContent() {
        return content;
    }

    public void setContent(List<ReporteResponse> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
