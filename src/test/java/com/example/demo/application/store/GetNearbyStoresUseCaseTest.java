package com.example.demo.application.store;

import com.example.demo.domain.store.adaptor.StoreAdaptor;
import com.example.demo.domain.store.entity.Address;
import com.example.demo.domain.store.entity.BusinessType;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.exception.StoreHandler;
import com.example.demo.domain.store.util.DistanceUtils;
import com.example.demo.presentation.store.dto.StoreRequest;
import com.example.demo.presentation.store.dto.StoreResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetNearbyStoresUseCaseTest {

    @Mock
    private StoreAdaptor storeAdaptor;

    @InjectMocks
    private GetNearbyStoresUseCase useCase;

    private Store createStore(String name, Double lat, Double lon) {
        Address address = new Address("서울특별시", "중구", "서울특별시 중구 세종대로 110", lat, lon);
        return Store.builder()
            .storeName(name)
            .businessType(BusinessType.RESTAURANT)
            .contactNumber("02-0000-0000")
            .address(address)
            .build();
    }

    private Store createStoreWithoutCoordinates(String name) {
        Address address = new Address("서울특별시", "중구", "서울특별시 중구 세종대로 110", null, null);
        return Store.builder()
            .storeName(name)
            .businessType(BusinessType.RESTAURANT)
            .contactNumber("02-0000-0000")
            .address(address)
            .build();
    }

    @Nested
    @DisplayName("반경 지정 케이스")
    class WithRadius {

        @Test
        @DisplayName("반경이 지정되면 어댑터 위임 및 매핑이 수행된다")
        void execute_delegatesToAdaptorAndMaps_whenRadiusProvided() {
            double userLat = 37.5665;
            double userLon = 126.9780;
            int limit = 3;
            double radiusKm = 2.0;

            Store storeNear = createStore("A", 37.5665, 126.9780);
            Store storeFar = createStore("B", 37.5651, 126.98955);
            List<Store> content = List.of(storeNear, storeFar);

            Page<Store> page = new PageImpl<>(content, PageRequest.of(0, limit), content.size());
            when(storeAdaptor.queryStoresWithinRadius(eq(userLat), eq(userLon), eq(radiusKm), any(PageRequest.class)))
                .thenReturn(page);

            StoreRequest.GetNearbyStores request = StoreRequest.GetNearbyStores.builder()
                .latitude(userLat)
                .longitude(userLon)
                .limit(limit)
                .radiusKm(radiusKm)
                .build();

            StoreResponse.NearbyStoreList response = useCase.execute(request);

            verify(storeAdaptor).queryStoresWithinRadius(eq(userLat), eq(userLon), eq(radiusKm), eq(PageRequest.of(0, limit)));
            assertThat(response.getTotalCount()).isEqualTo(2);
            assertThat(response.getStores()).hasSize(2);

            double expected0 = round2(DistanceUtils.calculateDistanceKm(userLat, userLon,
                storeNear.getAddress().getLatitude(), storeNear.getAddress().getLongitude()));
            double expected1 = round2(DistanceUtils.calculateDistanceKm(userLat, userLon,
                storeFar.getAddress().getLatitude(), storeFar.getAddress().getLongitude()));

            assertThat(response.getStores().get(0).getDistanceKm()).isEqualTo(expected0);
            assertThat(response.getStores().get(1).getDistanceKm()).isEqualTo(expected1);
            assertThat(response.getMaxDistanceKm()).isEqualTo(Math.max(expected0, expected1));
            assertThat(response.getUserLocation().getLatitude()).isEqualTo(userLat);
            assertThat(response.getUserLocation().getLongitude()).isEqualTo(userLon);
        }

        @Test
        @DisplayName("반경 조회 결과가 비어도 응답은 정상 매핑된다")
        void execute_handlesEmptyPage_whenRadiusProvided() {
            double userLat = 37.5665;
            double userLon = 126.9780;
            int limit = 5;
            double radiusKm = 1.0;

            when(storeAdaptor.queryStoresWithinRadius(eq(userLat), eq(userLon), eq(radiusKm), any(PageRequest.class)))
                .thenReturn(Page.empty());

            StoreRequest.GetNearbyStores request = StoreRequest.GetNearbyStores.builder()
                .latitude(userLat)
                .longitude(userLon)
                .limit(limit)
                .radiusKm(radiusKm)
                .build();

            StoreResponse.NearbyStoreList response = useCase.execute(request);

            assertThat(response.getStores()).isEmpty();
            assertThat(response.getTotalCount()).isEqualTo(0);
            assertThat(response.getMaxDistanceKm()).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("반경 미지정 케이스")
    class WithoutRadius {

        @Test
        @DisplayName("반경이 없으면 거리 오름차순 정렬 후 limit 만큼 반환된다")
        void execute_sortsByDistanceAndLimits_whenNoRadius() {
            double userLat = 37.5665;
            double userLon = 126.9780;
            int limit = 2;

            Store store0 = createStore("A", 37.5665, 126.9780);
            Store store1 = createStore("B", 37.5651, 126.98955);
            Store store2 = createStore("C", 37.5700, 126.9900);
            Store storeInvalid = createStoreWithoutCoordinates("D");

            List<Store> all = new ArrayList<>();
            all.add(store2);
            all.add(storeInvalid);
            all.add(store1);
            all.add(store0);

            when(storeAdaptor.queryStoresWithCoordinates()).thenReturn(all);

            StoreRequest.GetNearbyStores request = StoreRequest.GetNearbyStores.builder()
                .latitude(userLat)
                .longitude(userLon)
                .limit(limit)
                .radiusKm(null)
                .build();

            StoreResponse.NearbyStoreList response = useCase.execute(request);

            assertThat(response.getStores()).hasSize(2);

            double d0 = round2(DistanceUtils.calculateDistanceKm(userLat, userLon,
                store0.getAddress().getLatitude(), store0.getAddress().getLongitude()));
            double d1 = round2(DistanceUtils.calculateDistanceKm(userLat, userLon,
                store1.getAddress().getLatitude(), store1.getAddress().getLongitude()));

            assertThat(response.getStores().get(0).getStoreName()).isEqualTo("A");
            assertThat(response.getStores().get(0).getDistanceKm()).isEqualTo(d0);
            assertThat(response.getStores().get(1).getStoreName()).isEqualTo("B");
            assertThat(response.getStores().get(1).getDistanceKm()).isEqualTo(d1);
            assertThat(response.getMaxDistanceKm()).isEqualTo(Math.max(d0, d1));
        }

        @Test
        @DisplayName("좌표 리스트가 비어도 응답은 정상 매핑된다")
        void execute_handlesEmptyList_whenNoRadius() {
            double userLat = 37.5665;
            double userLon = 126.9780;
            int limit = 10;

            when(storeAdaptor.queryStoresWithCoordinates()).thenReturn(List.of());

            StoreRequest.GetNearbyStores request = StoreRequest.GetNearbyStores.builder()
                .latitude(userLat)
                .longitude(userLon)
                .limit(limit)
                .build();

            StoreResponse.NearbyStoreList response = useCase.execute(request);

            assertThat(response.getStores()).isEmpty();
            assertThat(response.getTotalCount()).isEqualTo(0);
            assertThat(response.getMaxDistanceKm()).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("검증 예외 케이스")
    class ValidationCases {

        @Test
        @DisplayName("위도/경도 null이면 StoreHandler 발생")
        void execute_throws_whenNullCoordinates() {
            StoreRequest.GetNearbyStores request = StoreRequest.GetNearbyStores.builder()
                .latitude(null)
                .longitude(126.9780)
                .limit(5)
                .build();

            assertThatThrownBy(() -> useCase.execute(request)).isInstanceOf(StoreHandler.class);
        }

        @Test
        @DisplayName("위도/경도 범위 벗어나면 StoreHandler 발생")
        void execute_throws_whenOutOfRangeCoordinates() {
            StoreRequest.GetNearbyStores request = StoreRequest.GetNearbyStores.builder()
                .latitude(100.0)
                .longitude(200.0)
                .limit(5)
                .build();

            assertThatThrownBy(() -> useCase.execute(request)).isInstanceOf(StoreHandler.class);
        }

        @Test
        @DisplayName("limit null 또는 0 이하면 IllegalArgumentException 발생")
        void execute_throws_whenInvalidLimit() {
            StoreRequest.GetNearbyStores request1 = StoreRequest.GetNearbyStores.builder()
                .latitude(37.5665)
                .longitude(126.9780)
                .limit(null)
                .build();

            StoreRequest.GetNearbyStores request2 = StoreRequest.GetNearbyStores.builder()
                .latitude(37.5665)
                .longitude(126.9780)
                .limit(0)
                .build();

            assertThatThrownBy(() -> useCase.execute(request1)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> useCase.execute(request2)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("반경이 0 이하이거나 100 초과면 StoreHandler 발생")
        void execute_throws_whenInvalidRadius() {
            StoreRequest.GetNearbyStores request1 = StoreRequest.GetNearbyStores.builder()
                .latitude(37.5665)
                .longitude(126.9780)
                .limit(5)
                .radiusKm(0.0)
                .build();

            StoreRequest.GetNearbyStores request2 = StoreRequest.GetNearbyStores.builder()
                .latitude(37.5665)
                .longitude(126.9780)
                .limit(5)
                .radiusKm(1000.0)
                .build();

            assertThatThrownBy(() -> useCase.execute(request1)).isInstanceOf(StoreHandler.class);
            assertThatThrownBy(() -> useCase.execute(request2)).isInstanceOf(StoreHandler.class);
        }
    }

    @Test
    @DisplayName("사용자와 동일 좌표면 거리 0이 반환된다")
    void execute_distanceZero_whenSameLocation() {
        double userLat = 37.5665;
        double userLon = 126.9780;

        Store storeSame = createStore("Same", userLat, userLon);
        when(storeAdaptor.queryStoresWithCoordinates()).thenReturn(List.of(storeSame));

        StoreRequest.GetNearbyStores request = StoreRequest.GetNearbyStores.builder()
            .latitude(userLat)
            .longitude(userLon)
            .limit(1)
            .build();

        StoreResponse.NearbyStoreList response = useCase.execute(request);
        assertThat(response.getStores()).hasSize(1);
        assertThat(response.getStores().get(0).getDistanceKm()).isEqualTo(0.0);
        assertThat(response.getMaxDistanceKm()).isEqualTo(0.0);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}


