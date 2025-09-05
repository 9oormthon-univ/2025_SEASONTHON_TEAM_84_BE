package com.example.demo.domain.store.entity;

import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 업종 분류를 나타내는 열거형
 */
@Getter
@RequiredArgsConstructor
public enum BusinessType {
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
     * 문자열로부터 BusinessType을 찾아 반환
     */
    public static BusinessType fromString(String businessType) {
        if (businessType == null || businessType.trim().isEmpty()) {
            return ETC;
        }
        final String normalized = normalize(businessType.trim());
        BusinessType mapped = TEXT_TO_TYPE.get(normalized);
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

    // 엑셀에서 오는 원본 라벨 전체를 정확 매핑
    private static final Map<String, BusinessType> TEXT_TO_TYPE = Map.ofEntries(
        // 한식 계열 및 외식/요식업 전반 → RESTAURANT
        Map.entry(normalize("한식_육류"), RESTAURANT),
        Map.entry(normalize("한식_일반"), RESTAURANT),
        Map.entry(normalize("기타요식업"), RESTAURANT),
        Map.entry(normalize("기타외식업"), RESTAURANT),
        Map.entry(normalize("한식_면류"), RESTAURANT),
        Map.entry(normalize("숙박업"), RESTAURANT), // 외식업 아님이나 기본 RESTAURANT로 분류 요청 없으므로 ETC 고려 가능
        Map.entry(normalize("중식"), RESTAURANT),
        Map.entry(normalize("한식_기타"), RESTAURANT),
        Map.entry(normalize("일식"), RESTAURANT),
        Map.entry(normalize("한식_분식"), RESTAURANT),
        Map.entry(normalize("한식_찌개류"), RESTAURANT),
        Map.entry(normalize("한식"), RESTAURANT),
        Map.entry(normalize("한실_일반"), RESTAURANT), // 오탈자 추정 → 한식 계열 처리
        Map.entry(normalize("베트남음식"), RESTAURANT),
        Map.entry(normalize("한식_한정식"), RESTAURANT),
        Map.entry(normalize("한식_해산물"), RESTAURANT),
        Map.entry(normalize("한식-육류"), RESTAURANT),
        Map.entry(normalize("한식-일반"), RESTAURANT),
        Map.entry(normalize("한식(일반)"), RESTAURANT),
        Map.entry(normalize("한식(육류)"), RESTAURANT),
        Map.entry(normalize("한식(면류)"), RESTAURANT),
        Map.entry(normalize("한식-면류"), RESTAURANT),

        // 카페/커피
        Map.entry(normalize("커피전문점"), CAFE),

        // 베이커리/제과
        Map.entry(normalize("베이커리"), BAKERY),
        Map.entry(normalize("제과점"), BAKERY),
        Map.entry(normalize("제과점업"), BAKERY),

        // 미용/이미용/이용/피부/네일 → BEAUTY
        Map.entry(normalize("미용업"), BEAUTY),
        Map.entry(normalize("이용업"), BEAUTY),
        Map.entry(normalize("미용실"), BEAUTY),
        Map.entry(normalize("이미용업"), BEAUTY),
        Map.entry(normalize("피부미용"), BEAUTY),
        Map.entry(normalize("네일서비스업"), BEAUTY),
        Map.entry(normalize("미용업_일반"), BEAUTY),

        // 세탁 → LAUNDRY
        Map.entry(normalize("세탁업"), LAUNDRY),

        // 이하 비요식/개인서비스/교육/정비/사진/독서실/목욕/세차/체력단련 등 → ETC
        Map.entry(normalize("기타비요식업"), ETC),
        Map.entry(normalize("교육서비스"), ETC),
        Map.entry(normalize("사진"), ETC),
        Map.entry(normalize("정비업"), ETC),
        Map.entry(normalize("베트남음식"), RESTAURANT),
        Map.entry(normalize("사진서비스업"), ETC),
        Map.entry(normalize("사진업"), ETC),
        Map.entry(normalize("독서실업"), ETC),
        Map.entry(normalize("세차업"), ETC),
        Map.entry(normalize("목욕업"), ETC),
        Map.entry(normalize("체력단련업"), ETC),
        Map.entry(normalize("기타개인서비스업"), ETC),
        Map.entry(normalize("기타비요식업(세차)"), ETC)
    );

    /**
     * 상세 카테고리(대분류/소분류) 매핑
     * - 대분류: 한식/중식/일식/카페/베이커리/미용/세탁/숙박/교육/사진/정비/외식업/요식업/비요식업/개인서비스/목욕/체력단련/아시아 등
     * - 소분류: 육류/면류/분식/찌개류/한정식/해산물/일반/제과점/커피전문점/세차 등
     */
    private static final Map<String, Detail> TEXT_TO_DETAIL = Map.ofEntries(
        // 한식 세부
        Map.entry(normalize("한식_육류"), new Detail("한식", "육류")),
        Map.entry(normalize("한식_일반"), new Detail("한식", "일반")),
        Map.entry(normalize("한식_면류"), new Detail("한식", "면류")),
        Map.entry(normalize("한식_기타"), new Detail("한식", "기타")),
        Map.entry(normalize("한식_분식"), new Detail("한식", "분식")),
        Map.entry(normalize("한식_찌개류"), new Detail("한식", "찌개류")),
        Map.entry(normalize("한식_한정식"), new Detail("한식", "한정식")),
        Map.entry(normalize("한식_해산물"), new Detail("한식", "해산물")),
        Map.entry(normalize("한식"), new Detail("한식", "일반")),
        Map.entry(normalize("한실_일반"), new Detail("한식", "일반")),
        Map.entry(normalize("한식-육류"), new Detail("한식", "육류")),
        Map.entry(normalize("한식-일반"), new Detail("한식", "일반")),
        Map.entry(normalize("한식(일반)"), new Detail("한식", "일반")),
        Map.entry(normalize("한식(육류)"), new Detail("한식", "육류")),
        Map.entry(normalize("한식(면류)"), new Detail("한식", "면류")),
        Map.entry(normalize("한식-면류"), new Detail("한식", "면류")),

        // 중식/일식/아시아
        Map.entry(normalize("중식"), new Detail("중식", "일반")),
        Map.entry(normalize("일식"), new Detail("일식", "일반")),
        Map.entry(normalize("베트남음식"), new Detail("아시아", "베트남")),

        // 카페/커피
        Map.entry(normalize("커피전문점"), new Detail("카페", "커피전문점")),

        // 베이커리
        Map.entry(normalize("베이커리"), new Detail("베이커리", "일반")),
        Map.entry(normalize("제과점"), new Detail("베이커리", "제과점")),
        Map.entry(normalize("제과점업"), new Detail("베이커리", "제과점")),

        // 미용/이용/피부/네일
        Map.entry(normalize("미용업"), new Detail("미용", "미용업")),
        Map.entry(normalize("미용업_일반"), new Detail("미용", "일반")),
        Map.entry(normalize("미용실"), new Detail("미용", "미용실")),
        Map.entry(normalize("이미용업"), new Detail("미용", "이미용업")),
        Map.entry(normalize("이용업"), new Detail("미용", "이용업")),
        Map.entry(normalize("피부미용"), new Detail("미용", "피부미용")),
        Map.entry(normalize("네일서비스업"), new Detail("미용", "네일서비스업")),

        // 세탁
        Map.entry(normalize("세탁업"), new Detail("세탁", "일반")),

        // 숙박
        Map.entry(normalize("숙박업"), new Detail("숙박", "일반")),

        // 교육/독서실
        Map.entry(normalize("교육서비스"), new Detail("교육", "일반")),
        Map.entry(normalize("독서실업"), new Detail("교육", "독서실")),

        // 사진
        Map.entry(normalize("사진"), new Detail("사진", "일반")),
        Map.entry(normalize("사진업"), new Detail("사진", "업")),
        Map.entry(normalize("사진서비스업"), new Detail("사진", "서비스업")),

        // 정비
        Map.entry(normalize("정비업"), new Detail("정비", "일반")),

        // 외식/요식 기타
        Map.entry(normalize("기타외식업"), new Detail("외식업", "기타")),
        Map.entry(normalize("기타요식업"), new Detail("요식업", "기타")),

        // 비요식/개인서비스/목욕/체력단련/세차
        Map.entry(normalize("기타비요식업"), new Detail("비요식업", "기타")),
        Map.entry(normalize("기타비요식업(세차)"), new Detail("비요식업", "세차")),
        Map.entry(normalize("세차업"), new Detail("비요식업", "세차업")),
        Map.entry(normalize("기타개인서비스업"), new Detail("개인서비스", "기타")),
        Map.entry(normalize("목욕업"), new Detail("목욕", "일반")),
        Map.entry(normalize("체력단련업"), new Detail("체력단련", "일반"))
    );

    public static Classification classify(String businessTypeLabel) {
        if (businessTypeLabel == null || businessTypeLabel.trim().isEmpty()) {
            return new Classification(ETC, "기타", "일반");
        }
        String key = normalize(businessTypeLabel.trim());
        BusinessType type = fromString(businessTypeLabel);
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

    public static record Classification(BusinessType type, String majorCategory, String subCategory) {}
    private static record Detail(String major, String sub) {}
}
