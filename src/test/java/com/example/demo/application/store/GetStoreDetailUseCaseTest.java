package com.example.demo.application.store;

import com.example.demo.domain.store.adaptor.StoreAdaptor;
import com.example.demo.domain.store.entity.Category;
import com.example.demo.domain.store.entity.Store;
import com.example.demo.domain.store.entity.StoreMenu;
import com.example.demo.presentation.store.dto.StoreResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetStoreDetailUseCaseTest {

    @Mock
    private StoreAdaptor storeAdaptor;

    @InjectMocks
    private GetStoreDetailUseCase useCase;

    @Test
    void execute_returnsStoreDetailDto_fromStoreFetchedByAdaptor() {
        // given
        Long storeId = 10L;

        // Address mock
        com.example.demo.domain.store.entity.Address address = mock(com.example.demo.domain.store.entity.Address.class);
        given(address.getSido()).willReturn("서울특별시");
        given(address.getSigun()).willReturn("중구");
        given(address.getFullAddress()).willReturn("서울 중구 세종대로 110");
        given(address.getLatitude()).willReturn(37.5665);
        given(address.getLongitude()).willReturn(126.9780);

        // Menu mocks
        StoreMenu menu1 = mock(StoreMenu.class);
        given(menu1.getId()).willReturn(1L);
        given(menu1.getMenuName()).willReturn("김치찌개");
        given(menu1.getPrice()).willReturn(BigDecimal.valueOf(9000));
        given(menu1.getMenuOrder()).willReturn(1);

        StoreMenu menu2 = mock(StoreMenu.class);
        given(menu2.getId()).willReturn(2L);
        given(menu2.getMenuName()).willReturn("된장찌개");
        given(menu2.getPrice()).willReturn(BigDecimal.valueOf(8000));
        given(menu2.getMenuOrder()).willReturn(2);

        // Store mock
        Store store = mock(Store.class);
        given(store.getId()).willReturn(storeId);
        given(store.getStoreName()).willReturn("착한식당");
        given(store.getCategory()).willReturn(Category.RESTAURANT);
        given(store.getMajorCategory()).willReturn("한식");
        given(store.getSubCategory()).willReturn("찌개류");
        given(store.getContactNumber()).willReturn("02-1234-5678");
        given(store.getAddress()).willReturn(address);
        given(store.getMenus()).willReturn(List.of(menu1, menu2));
        given(store.isActive()).willReturn(true);
        LocalDateTime created = LocalDateTime.now().minusDays(1);
        LocalDateTime modified = LocalDateTime.now();
        given(store.getCreatedDate()).willReturn(created);
        given(store.getLastModifiedDate()).willReturn(modified);

        given(storeAdaptor.queryByIdFetchMenu(eq(storeId))).willReturn(store);

        // when
        StoreResponse.StoreDetail result = useCase.execute(storeId);

        // then
        verify(storeAdaptor).queryByIdFetchMenu(eq(storeId));
        assertThat(result).isNotNull();
        assertThat(result.getStoreId()).isEqualTo(storeId);
        assertThat(result.getStoreName()).isEqualTo("착한식당");
        assertThat(result.getCategory()).isEqualTo(Category.RESTAURANT);
        assertThat(result.getCategoryDescription()).isEqualTo(Category.RESTAURANT.getDescription());
        assertThat(result.getMajorCategory()).isEqualTo("한식");
        assertThat(result.getSubCategory()).isEqualTo("찌개류");
        assertThat(result.getContactNumber()).isEqualTo("02-1234-5678");
        assertThat(result.getAddress()).isNotNull();
        assertThat(result.getAddress().getFullAddress()).isEqualTo("서울 중구 세종대로 110");
        assertThat(result.getMenus()).hasSize(2);
        assertThat(result.getMenus().get(0).getMenuName()).isEqualTo("김치찌개");
        assertThat(result.getMenus().get(0).getPrice()).isEqualTo(BigDecimal.valueOf(9000));
        assertThat(result.getMenus().get(1).getMenuName()).isEqualTo("된장찌개");
        assertThat(result.getMenus().get(1).getPrice()).isEqualTo(BigDecimal.valueOf(8000));
        assertThat(result.isActive()).isTrue();
        assertThat(result.getCreatedDate()).isEqualTo(created);
        assertThat(result.getLastModifiedDate()).isEqualTo(modified);
    }
}


