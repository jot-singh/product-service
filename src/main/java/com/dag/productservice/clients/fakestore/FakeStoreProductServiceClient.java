package com.dag.productservice.clients.fakestore;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.dag.productservice.dto.FakeStoreResponseDto;
import com.dag.productservice.dto.ProductRequestDto;
import com.dag.productservice.dto.clients.ClientProductRequestDto;
import com.dag.productservice.exception.NotFoundException;

/* 
 * Fake Store Product Service Client Wrapper
 */
@Service
public class FakeStoreProductServiceClient {
    private RestTemplateBuilder restTemplateBuilder;


    @Value("${fakestore.api.url}")
    private String fakeStoreApiUrl;

    @Value("${fakestore.api.paths.product}")
    private String fakeStoreProductsApiPath;

    private String specificProductRequestUrl ;
    private String productRequestsBaseUrl ;

    public FakeStoreProductServiceClient(RestTemplateBuilder restTemplateBuilder,
                                         @Value("${fakestore.api.url}") String fakeStoreApiUrl,
                                         @Value("${fakestore.api.paths.product}") String fakeStoreProductsApiPath) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.productRequestsBaseUrl  = fakeStoreApiUrl + fakeStoreProductsApiPath;
        this.specificProductRequestUrl = fakeStoreApiUrl + fakeStoreProductsApiPath + "/{id}";
    }


    public FakeStoreResponseDto createProduct(ProductRequestDto productRequestDto) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<FakeStoreResponseDto> response = restTemplate.postForEntity(
                productRequestsBaseUrl, productRequestDto, FakeStoreResponseDto.class
        );

        return response.getBody();
    }

    public FakeStoreResponseDto getProductById(Long id) throws NotFoundException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<FakeStoreResponseDto> response =
                restTemplate.getForEntity(specificProductRequestUrl, FakeStoreResponseDto.class, id);

        FakeStoreResponseDto FakeStoreResponseDto = response.getBody();

        if (FakeStoreResponseDto == null) {
            throw new NotFoundException("Product with id: " + id + " doesn't exist.");
        }

        return FakeStoreResponseDto;
    }

    public List<FakeStoreResponseDto> getAllProducts() {
        RestTemplate restTemplate = restTemplateBuilder.build();

        ResponseEntity<FakeStoreResponseDto[]> response =
                restTemplate.getForEntity(productRequestsBaseUrl, FakeStoreResponseDto[].class);

        return Arrays.stream(response.getBody()).toList();
    }

    public FakeStoreResponseDto deleteProductById(Long id) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        RequestCallback requestCallback = restTemplate.acceptHeaderRequestCallback(ClientProductRequestDto.class);
        ResponseExtractor<ResponseEntity<FakeStoreResponseDto>> responseExtractor =
                restTemplate.responseEntityExtractor(FakeStoreResponseDto.class);
        ResponseEntity<FakeStoreResponseDto> response = restTemplate.execute(specificProductRequestUrl, HttpMethod.DELETE,
                requestCallback, responseExtractor, id);

        return Objects.nonNull(response) ? response.getBody() : null;
    }

    public FakeStoreResponseDto updateProductById(Long id, ProductRequestDto productRequestDto) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<FakeStoreResponseDto> response = restTemplate.postForEntity(
                productRequestsBaseUrl, productRequestDto, FakeStoreResponseDto.class
        );

        return response.getBody();
    }
}
