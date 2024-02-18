package id.arya.portofolio.ecommerce.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagingResponse {
    private Integer currentPage;
    private Integer totalPage;
    private Integer size;
}
