package com.dag.productservice.services.product;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import com.dag.productservice.clients.fakestore.FakeStoreProductServiceClient;
import com.dag.productservice.dto.FakeStoreResponseDto;
import com.dag.productservice.dto.ProductRequestDto;
import com.dag.productservice.dto.ProductResponseDto;
import com.dag.productservice.exceptionhandlers.exceptions.NotFoundException;
import com.dag.productservice.models.Price;

@Service
public class FakeStoreProductService implements ProductService {
    RestTemplateBuilder restTemplateBuilder;

    private FakeStoreProductServiceClient fakeStoryProductServiceClient;

    public FakeStoreProductService(FakeStoreProductServiceClient fakeStoryProductServiceClient) {
        this.fakeStoryProductServiceClient = fakeStoryProductServiceClient;
    }

    private ProductResponseDto convertFakeStoreProductIntoGenericProductResponse( FakeStoreResponseDto fakeStoreResponseDto) {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(String.valueOf(fakeStoreResponseDto.getId()));
        productResponseDto.setTitle(fakeStoreResponseDto.getTitle());

        // Create Price object from Double
        if (fakeStoreResponseDto.getPrice() != null) {
            Price price = new Price();
            price.setPrice(fakeStoreResponseDto.getPrice());
            price.setCurrency("USD"); // Default currency for FakeStore
            productResponseDto.setPrice(price);
        }

        productResponseDto.setDescription(fakeStoreResponseDto.getDescription());
        productResponseDto.setCategory(fakeStoreResponseDto.getCategory());
        //productResponseDto.setImage(fakeStoreResponseDto.getImage());
        return productResponseDto;
    }

    public ProductResponseDto createProduct(ProductRequestDto product) {
        return convertFakeStoreProductIntoGenericProductResponse(fakeStoryProductServiceClient.createProduct(product));
    }

    public ProductResponseDto getProductById(String id) throws NotFoundException {
        FakeStoreResponseDto fakeStoreResponseDto = fakeStoryProductServiceClient.getProductById(Long.parseLong(id));
        if (fakeStoreResponseDto == null) {
            throwNotFoundException();
        }
        return convertFakeStoreProductIntoGenericProductResponse(fakeStoreResponseDto);
    }
    @Override
    public List<ProductResponseDto> getAllProducts() {
        return fakeStoryProductServiceClient.getAllProducts().stream()
                .map(this::convertFakeStoreProductIntoGenericProductResponse)
                .collect(Collectors.toList());
    }
    @Override
    public ProductResponseDto deleteProductById(String id) {
        FakeStoreResponseDto fakeStoreResponseDto = this.fakeStoryProductServiceClient.deleteProductById(Long.parseLong(id));
        if (fakeStoreResponseDto == null) {
            throwNotFoundException();
        }   
        return convertFakeStoreProductIntoGenericProductResponse(fakeStoreResponseDto);
    }

    private void throwNotFoundException() {
        throw new NotFoundException("productId Not Found");
    }

    @Override
    public ProductResponseDto updateProductById(String id, ProductRequestDto requestDto) {
        FakeStoreResponseDto fakeStoreResponseDto = fakeStoryProductServiceClient.updateProductById(Long.parseLong(id), requestDto);
        //Add Null check and throw NotFoundException if null
        if (fakeStoreResponseDto == null) {
            throwNotFoundException();
        }
        return convertFakeStoreProductIntoGenericProductResponse(fakeStoreResponseDto);
    }
}
