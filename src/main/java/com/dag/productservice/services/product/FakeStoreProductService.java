package com.dag.productservice.services.product;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.dag.productservice.dto.ProductRequestDto;
import com.dag.productservice.dto.ProductResponseDto;
import com.dag.productservice.exceptionhandlers.exceptions.NotFoundException;

@Service
public class FakeStoreProductService implements ProductService {
    RestTemplateBuilder restTemplateBuilder;

    @Value("${fakestore.api.url}")
    String fakeStoreUrl;

    private final RestTemplate restTemplate;

    FakeStoreProductService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.restTemplate = restTemplateBuilder.build();
    }

    public ProductResponseDto getProductById(String id) {
        String getUri = getUriForId();

        // ResponseDto pojo need not be exactly same as the response json. It can have
        // subset of total fields coming
        // in the response json.
        ResponseEntity<ProductResponseDto> fakeStoreResponseEntity = restTemplate
                .getForEntity(getUri, ProductResponseDto.class, id);

        return fakeStoreResponseEntity.getBody();
    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        ResponseEntity<ProductResponseDto> response = restTemplate
                .postForEntity(postUri(), requestDto, ProductResponseDto.class);
        return response.getBody();
    }

    @Override
    public ProductResponseDto[] getAllProducts() {
        ResponseEntity<ProductResponseDto[]> allProducts = restTemplate.getForEntity(getUri(), ProductResponseDto[].class);
        return allProducts.getBody();
    }

    @Override
    public ProductResponseDto deleteproductById(String id) {
        // if no response is needed.
        // this.restTemplate.delete(deleteUri(),id);
        // if we want to return the responseDto.
        RequestCallback requestCallback = this.restTemplate.acceptHeaderRequestCallback(ProductResponseDto.class);

        ResponseExtractor<ResponseEntity<ProductResponseDto>> responseExtractor = this.restTemplate
                .responseEntityExtractor(ProductResponseDto.class);

        Optional<ResponseEntity<ProductResponseDto>> responseEntity = Optional.of(Objects.requireNonNull(this.restTemplate.execute(deleteUri(),
                HttpMethod.DELETE, requestCallback, responseExtractor, id)));
        if(responseEntity.get().getBody() == null)
            throwNotFoundException();
        return responseEntity.orElseThrow(() -> new NotFoundException("ProductId not found")).getBody();
    }

    private void throwNotFoundException() {
        throw new NotFoundException("productId Not Found");
    }

    @Override
    public ProductResponseDto updateProductById(String id, ProductRequestDto requestDto) {
        RequestCallback requestCallback = this.restTemplate.httpEntityCallback(requestDto, ProductResponseDto.class);
        ResponseExtractor<ResponseEntity<ProductResponseDto>> responseExtractor = this.restTemplate
                .responseEntityExtractor(ProductResponseDto.class);
        ResponseEntity<ProductResponseDto> responseEntity = this.restTemplate.execute(updateUri(), HttpMethod.PUT,
                requestCallback, responseExtractor, id);
        if (Objects.requireNonNull(responseEntity).getBody() == null)
            throwNotFoundException();
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

    private String updateUri() {
        return byId();
    }

    private String byId() {
        return String.join("/", fakeStoreUrl, "{id}");
    }
}