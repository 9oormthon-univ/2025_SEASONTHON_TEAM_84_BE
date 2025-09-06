package com.example.demo.domain.store.entity;

import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Collections;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 업종(카테고리) 분류를 나타내는 열거형
 */
@Getter
@RequiredArgsConstructor
public enum Category {
    RESTAURANT("음식점"),
    CAFE("카페"),
    CONVENIENCE_STORE("편의점"),
    SUPERMARKET("마트"),
    BAKERY("제과점"),
    PHARMACY("약국"),
    HOSPITAL("병원"),
    BEAUTY("미용실"),
    LAUNDRY("세탁소"),
    GAS_STATION("주유소"),
    ETC("기타");

    private final String description;

    /**
     * 문자열로부터 Category을 찾아 반환
     */
    public static Category fromString(String businessType) {
        if (businessType == null || businessType.trim().isEmpty()) {
            return ETC;
        }
        final String normalized = normalize(businessType.trim());
        Category mapped = TEXT_TO_TYPE.get(normalized);
        return mapped != null ? mapped : ETC;
    }

    private static String normalize(String value) {
        // 공백 제거, 구분자 통일: '_', '-', '(', ')' 제거
        return value
            .replaceAll("\\s+", "")
            .replace("_", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "");
    }

    // 엑셀에서 오는 원본 라벨 전체를 정확 매핑 (중복 키 방지 위해 putIfAbsent 사용)
    private static final Map<String, Category> TEXT_TO_TYPE = new LinkedHashMap<>();
    private static final Map<String, Detail> TEXT_TO_DETAIL = new LinkedHashMap<>();

    private static void addType(String label, Category type) {
        TEXT_TO_TYPE.putIfAbsent(normalize(label), type);
    }

    private static void addDetail(String label, String major, String sub) {
        TEXT_TO_DETAIL.putIfAbsent(normalize(label), new Detail(major, sub));
    }

    static {
        // TYPE 매핑
        addType("한식_육류", RESTAURANT);
        addType("한식_일반", RESTAURANT);
        addType("기타요식업", RESTAURANT);
        addType("기타외식업", RESTAURANT);
        addType("한식_면류", RESTAURANT);
        addType("숙박업", RESTAURANT);
        addType("중식", RESTAURANT);
        addType("한식_기타", RESTAURANT);
        addType("일식", RESTAURANT);
        addType("한식_분식", RESTAURANT);
        addType("한식_찌개류", RESTAURANT);
        addType("한식", RESTAURANT);
        addType("한실_일반", RESTAURANT);
        addType("베트남음식", RESTAURANT);
        addType("한식_한정식", RESTAURANT);
        addType("한식_해산물", RESTAURANT);
        addType("한식-육류", RESTAURANT);
        addType("한식-일반", RESTAURANT);
        addType("한식(일반)", RESTAURANT);
        addType("한식(육류)", RESTAURANT);
        addType("한식(면류)", RESTAURANT);
        addType("한식-면류", RESTAURANT);
        addType("양식", RESTAURANT);

        addType("커피전문점", CAFE);

        addType("베이커리", BAKERY);
        addType("제과점", BAKERY);
        addType("제과점업", BAKERY);

        addType("미용업", BEAUTY);
        addType("이용업", BEAUTY);
        addType("미용실", BEAUTY);
        addType("이미용업", BEAUTY);
        addType("피부미용", BEAUTY);
        addType("네일서비스업", BEAUTY);
        addType("미용업_일반", BEAUTY);

        addType("세탁업", LAUNDRY);

        addType("기타비요식업", ETC);
        addType("교육서비스", ETC);
        addType("사진", ETC);
        addType("정비업", ETC);
        addType("사진서비스업", ETC);
        addType("사진업", ETC);
        addType("독서실업", ETC);
        addType("세차업", ETC);
        addType("목욕업", ETC);
        addType("체력단련업", ETC);
        addType("기타개인서비스업", ETC);
        addType("기타비요식업(세차)", ETC);

        // DETAIL 매핑
        addDetail("한식_육류", "한식", "육류");
        addDetail("한식_일반", "한식", "일반");
        addDetail("한식_면류", "한식", "면류");
        addDetail("한식_기타", "한식", "기타");
        addDetail("한식_분식", "한식", "분식");
        addDetail("한식_찌개류", "한식", "찌개류");
        addDetail("한식_한정식", "한식", "한정식");
        addDetail("한식_해산물", "한식", "해산물");
        addDetail("한식", "한식", "일반");
        addDetail("한실_일반", "한식", "일반");
        addDetail("한식-육류", "한식", "육류");
        addDetail("한식-일반", "한식", "일반");
        addDetail("한식(일반)", "한식", "일반");
        addDetail("한식(육류)", "한식", "육류");
        addDetail("한식(면류)", "한식", "면류");
        addDetail("한식-면류", "한식", "면류");

        addDetail("중식", "중식", "일반");
        addDetail("일식", "일식", "일반");
        addDetail("베트남음식", "아시아", "베트남");
        addDetail("양식", "양식", "일반");

        addDetail("커피전문점", "카페", "커피전문점");

        addDetail("베이커리", "베이커리", "일반");
        addDetail("제과점", "베이커리", "제과점");
        addDetail("제과점업", "베이커리", "제과점");

        addDetail("미용업", "미용", "미용업");
        addDetail("미용업_일반", "미용", "일반");
        addDetail("미용실", "미용", "미용실");
        addDetail("이미용업", "미용", "이미용업");
        addDetail("이용업", "미용", "이용업");
        addDetail("피부미용", "미용", "피부미용");
        addDetail("네일서비스업", "미용", "네일서비스업");

        addDetail("세탁업", "세탁", "일반");

        addDetail("숙박업", "숙박", "일반");

        addDetail("교육서비스", "교육", "일반");
        addDetail("독서실업", "교육", "독서실");

        addDetail("사진", "사진", "일반");
        addDetail("사진업", "사진", "업");
        addDetail("사진서비스업", "사진", "서비스업");

        addDetail("정비업", "정비", "일반");

        addDetail("기타외식업", "외식업", "기타");
        addDetail("기타요식업", "요식업", "기타");

        addDetail("기타비요식업", "비요식업", "기타");
        addDetail("기타비요식업(세차)", "비요식업", "세차");
        addDetail("세차업", "비요식업", "세차업");
        addDetail("기타개인서비스업", "개인서비스", "기타");
        addDetail("목욕업", "목욕", "일반");
        addDetail("체력단련업", "체력단련", "일반");
    }

    /**
     * 상세 카테고리(대분류/소분류) 매핑
     * - 대분류: 한식/중식/일식/카페/베이커리/미용/세탁/숙박/교육/사진/정비/외식업/요식업/비요식업/개인서비스/목욕/체력단련/아시아 등
     * - 소분류: 육류/면류/분식/찌개류/한정식/해산물/일반/제과점/커피전문점/세차 등
     */
    // 위에서 TEXT_TO_DETAIL을 채우므로 별도 Map.ofEntries 초기화는 사용하지 않습니다.

    public static Classification classify(String businessTypeLabel) {
        if (businessTypeLabel == null || businessTypeLabel.trim().isEmpty()) {
            return new Classification(ETC, "기타", "일반");
        }
        String key = normalize(businessTypeLabel.trim());
        Category type = fromString(businessTypeLabel);
        Detail detail = TEXT_TO_DETAIL.get(key);
        if (detail == null) {
            String major = switch (type) {
                case RESTAURANT -> "음식점";
                case CAFE -> "카페";
                case BAKERY -> "베이커리";
                case BEAUTY -> "미용";
                case LAUNDRY -> "세탁";
                case CONVENIENCE_STORE -> "편의점";
                case SUPERMARKET -> "마트";
                case PHARMACY -> "약국";
                case HOSPITAL -> "병원";
                case GAS_STATION -> "주유소";
                case ETC -> "기타";
            };
            return new Classification(type, major, "일반");
        }
        return new Classification(type, detail.major(), detail.sub());
    }

    /**
     * 대분류 → 소분류 목록 맵 (UI 선택용)
     * - 기존 TEXT_TO_DETAIL 매핑으로부터 역으로 구성한다.
     * - 순서는 선언(등장) 순서를 따르도록 LinkedHash* 를 사용한다.
     */
    private static final Map<String, List<String>> MAJOR_TO_SUBS;

    static {
        Map<String, LinkedHashSet<String>> temp = new LinkedHashMap<>();
        for (Detail detail : TEXT_TO_DETAIL.values()) {
            temp.computeIfAbsent(detail.major(), k -> new LinkedHashSet<>()).add(detail.sub());
        }
        Map<String, List<String>> finalized = new LinkedHashMap<>();
        for (Map.Entry<String, LinkedHashSet<String>> e : temp.entrySet()) {
            finalized.put(e.getKey(), List.copyOf(e.getValue()));
        }
        MAJOR_TO_SUBS = Collections.unmodifiableMap(finalized);
    }

    /**
     * 대분류 전체 목록 반환 (등장 순서 유지)
     */
    public static List<String> listMajors() {
        return List.copyOf(MAJOR_TO_SUBS.keySet());
    }

    /**
     * 특정 대분류에 속한 소분류 목록 반환 (없으면 빈 리스트)
     */
    public static List<String> listSubCategories(String majorCategory) {
        if (majorCategory == null) {
            return List.of();
        }
        List<String> subs = MAJOR_TO_SUBS.get(majorCategory.trim());
        return subs != null ? subs : List.of();
    }

    /**
     * 대분류→소분류 전체 맵 반환 (불변 뷰)
     */
    public static Map<String, List<String>> listMajorToSubcategories() {
        return MAJOR_TO_SUBS;
    }

    public static record Classification(Category type, String majorCategory, String subCategory) {}
    private static record Detail(String major, String sub) {}
}


