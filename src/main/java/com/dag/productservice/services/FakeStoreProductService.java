package com.dag.productservice.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dag.productservice.dto.schema.RequestDto;
import com.dag.productservice.dto.schema.ResponseDto;

@Service
public class FakeStoreProductService implements ProductService {
    RestTemplateBuilder restTemplateBuilder;
    
    @Value("${fakestore.api.url}")
    String fakeStoreUrl;
    
    FakeStoreProductService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    public ResponseDto getProductById(Long id) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        String getUri = getUriForId();
        
        //ResponseDto pojo need not be exactly same as the response json. It can have subset of total fields coming
        //in the response json. 
        ResponseEntity<ResponseDto> fakeStoreResponseEntity = restTemplate
                .getForEntity(getUri,ResponseDto.class, id);
        ResponseDto responseDto = fakeStoreResponseEntity.getBody();
        
        return responseDto;
    }

    @Override
    public ResponseDto createProduct(RequestDto requestDto) {
        RestTemplate restTemplate = this.restTemplateBuilder.build();
        ResponseEntity<ResponseDto> response = restTemplate
                                .postForEntity(postUri(), requestDto, ResponseDto.class);
        return response.getBody();
    }

    private String getUriForId() {
        return String.join("/", fakeStoreUrl,"{id}");
    }
    
    private String postUri() {
        return fakeStoreUrl;
    }
    


    /* Function to copy the response into ResponseDto */
    /* private void copyObject(FakeStoreResponseDto fakeStoreResponseDto, ResponseDto responseDto) {
            responseDto.setId(fakeStoreResponseDto.getId());
            responseDto.setCategory(fakeStoreResponseDto.getCategory());
            responseDto.setDescription(fakeStoreResponseDto.getDescription());
            responseDto.setPrice(fakeStoreResponseDto.getPrice());
            responseDto.setTitle(fakeStoreResponseDto.getTitle());
    } */
}