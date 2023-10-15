package com.dag.productservice.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.dag.productservice.dto.schema.RequestDto;
import com.dag.productservice.dto.schema.ResponseDto;

@Service
public class FakeStoreProductService implements ProductService {
    RestTemplateBuilder restTemplateBuilder;

    @Value("${fakestore.api.url}")
    String fakeStoreUrl;

    private RestTemplate restTemplate;

    FakeStoreProductService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.restTemplate = restTemplateBuilder.build();
    }

    public ResponseDto getProductById(Long id) {
        String getUri = getUriForId();

        // ResponseDto pojo need not be exactly same as the response json. It can have
        // subset of total fields coming
        // in the response json.
        ResponseEntity<ResponseDto> fakeStoreResponseEntity = restTemplate
                .getForEntity(getUri, ResponseDto.class, id);
    
        return fakeStoreResponseEntity.getBody();
    }

    @Override
    public ResponseDto createProduct(RequestDto requestDto) {
        ResponseEntity<ResponseDto> response = restTemplate
                .postForEntity(postUri(), requestDto, ResponseDto.class);
        return response.getBody();
    }

    @Override
    public ResponseDto[] getAllProducts() {
        ResponseEntity<ResponseDto[]> allProducts = restTemplate.getForEntity(getUri(), ResponseDto[].class);
        return allProducts.getBody();
    }

    @Override
    public ResponseDto deleteproductById(Integer id) {
        // if no response is needed.
        // this.restTemplate.delete(deleteUri(),id);
        // if we want to return the responseDto.
        RequestCallback requestCallback = this.restTemplate.acceptHeaderRequestCallback(ResponseDto.class);

        ResponseExtractor<ResponseEntity<ResponseDto>> responseExtractor = this.restTemplate
                .responseEntityExtractor(ResponseDto.class);

        Optional<ResponseEntity<ResponseDto>> responseEntity = Optional.of(this.restTemplate.execute(deleteUri(),
                HttpMethod.DELETE, requestCallback, responseExtractor, id));

        return responseEntity.orElseThrow().getBody();
    }

    @Override
    public ResponseDto updateProductById(Long id, RequestDto requestDto) {
        RequestCallback requestCallback = this.restTemplate.httpEntityCallback(requestDto, ResponseDto.class);
        ResponseExtractor<ResponseEntity<ResponseDto>> responseExtractor = this.restTemplate
                .responseEntityExtractor(ResponseDto.class);
        ResponseEntity<ResponseDto> responseEntity = this.restTemplate.execute(updateUri(), HttpMethod.PUT, requestCallback, responseExtractor, id);
        return responseEntity.getBody();
    }


    private String getUriForId() {
        return byId();
    }

    private String postUri() {
        return fakeStoreUrl;
    }

    private String getUri() {
        return fakeStoreUrl;
    }

    private String deleteUri() {
        return byId();
    }

    private String updateUri(){
        return  byId();
    }

    private String byId() {
        return String.join("/", fakeStoreUrl, "{id}");
    }

    /* Function to copy the response into ResponseDto */
    /*
     * private void copyObject(FakeStoreResponseDto fakeStoreResponseDto,
     * ResponseDto responseDto) {
     * responseDto.setId(fakeStoreResponseDto.getId());
     * responseDto.setCategory(fakeStoreResponseDto.getCategory());
     * responseDto.setDescription(fakeStoreResponseDto.getDescription());
     * responseDto.setPrice(fakeStoreResponseDto.getPrice());
     * responseDto.setTitle(fakeStoreResponseDto.getTitle());
     * }
     */
}