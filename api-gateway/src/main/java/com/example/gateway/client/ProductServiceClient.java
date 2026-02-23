//package com.example.gateway.client;
//
//import net.devh.boot.grpc.client.inject.GrpcClient;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//@Service
//public class ProductServiceClient {
//
//    @GrpcClient("product-service")
//    private ProductServiceGrpc.ProductServiceBlockingStub productStub;
//
//    public Mono<ProductResponse> getProducts() {
//        return Mono.fromCallable(() -> productStub.getProducts(GetProductsRequest.getDefaultInstance()))
//                   .onErrorMap(Exception::new);
//    }
//}