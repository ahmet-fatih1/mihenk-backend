# Rent-a-Car Yönetim Paneli — Mimari Kararlar
Son güncelleme: 12.09.2026 | Backend: Ahmet Fatih | Frontend: (arkadaş)

## 0. Teknoloji Yığını
- Java 21+ / Spring Boot 4.x
- PostgreSQL (+ btree_gist, pgcrypto, ileride pgvector)
- JPA/Hibernate, Flyway, MapStruct, Lombok (ölçülü)
- Spring Security 6 + kendi JWT'im (Keycloak'a geçiş kapısı açık)
- springdoc-openapi → frontend'e TS tipi üretimi
- Spring AI (tool calling)
- Test: JUnit 5 + Testcontainers
- Docker Compose: postgres + api + pgadmin

## 1. KARAR: Multi-tenancy → Shared DB + company_id ✅
- Tek DB, tek uygulama, her tabloda `company_id`
- Filtre altyapıda: Hibernate `@TenantId` + `TenantContext`
- İleride Postgres RLS (son savunma hattı)
- ⛔ `companyId` ASLA request body/query'den alınmaz — sadece JWT'den

## 2. KARAR: Paket yapısı → Özellik bazlı (modüler) ✅
com.rentacar
├── RentACarApplication (KÖKTE — 3 anotasyon silinecek)
├── common/ (entity, exception, dto, enums, util, security/TenantGuard)
├── config/ (Security, OpenApi, JpaAuditing, Cors)
├── tenancy/ (Company, Branch, Subscription, TenantContext)
├── security/ (AppUser, Role, Permission, JWT)
├── customer/  vehicle/  reservation/  rental/
├── maintenance/  pricing/  analytics/  ai/

Modül içi: Entity, Repository, Service(interface), ServiceImpl,
Controller, Mapper, Specifications, dto/
- Controller'da Impl YOK (tek sınıf)
- `I` öneki YOK → `CustomerService` / `CustomerServiceImpl`
- Modülde 4+ servis varsa `impl/` alt klasörü aç

## 3. KARAR: İş modeli / Abonelik ✅
- Bağımsız şube de "tek şubeli şirket" olarak modellenir (kodda çatal yok)
- company.type: INDEPENDENT | CORPORATE
- Plan: BRANCH_MONTHLY (aylık) | CORPORATE_ANNUAL (yıllık)
- subscription.branch_quota → şube ekleme limiti
- PAST_DUE: yeni işlem açılamaz, mevcut veri OKUNUR (veri rehin alınmaz)
- Gece job'ı: süresi geçenleri PAST_DUE yap + fatura üret + uyarı

## 4. KARAR: Roller (permission bazlı) ✅
SUPER_ADMIN | PLATFORM_SUPPORT(salt okuma) | COMPANY_OWNER
BRANCH_MANAGER | BRANCH_STAFF | ACCOUNTANT(ops.)
- Yetki: `@PreAuthorize("hasAuthority('vehicle:write')")`
- Kapsam: `TenantGuard.assertBranchAccess(branchId)`
- AI tool'ları yetkiyi KENDİ İÇİNDE kontrol eder (prompt koruma değildir)

## 5. KARAR: Veritabanı standartları ✅
- Para: numeric(19,4) + BigDecimal (double YASAK)
- Zaman: timestamptz + OffsetDateTime, uygulama UTC
- PK: bigint identity (dışa açık gerekirse public_id UUID)
- Enum: DB'de varchar + @Enumerated(EnumType.STRING)
- Soft delete: deleted_at + @SQLRestriction
- Audit: created_at/by, updated_at/by → BaseEntity + AuditorAware
- Migration: Flyway; ddl-auto: validate (update YASAK)

## 6. KARAR: Çifte rezervasyon koruması (3 kademe) ✅
1) Uygulama: müsaitlik sorgusu (kullanıcı mesajı için)
2) Kilit: PESSIMISTIC_WRITE / @Version
3) DB garantisi: EXCLUDE USING gist ... DEFERRABLE INITIALLY IMMEDIATE
   (status IN ('CONFIRMED','ACTIVE') koşullu)

Kriz müdahalesi:
- Kademe 1: SET CONSTRAINTS ... DEFERRED (transaction içinde)
- Kademe 2: status = 'ON_HOLD' → düzelt → CONFIRMED (en pratik)
- Kademe 3: POST /admin/reservations/{id}/override (reason ZORUNLU)
- Her müdahale → admin_action_log (actor, reason, old/new jsonb, ip)

## 7. KARAR: vehicle_status_history ZORUNLU ✅
- vehicle.status (enum) = ANLIK durum, hızlı sorgu
- vehicle_status_history = ZAMAN ARALIKLI geçmiş (started_at, ended_at)
- Statü değişimi SADECE VehicleService.changeStatus() içinden geçer
- Bu tablo olmadan doluluk/downtime HESAPLANAMAZ ve veri geri gelmez

## 8. KARAR: DTO & okuma stratejisi ✅
- Entity servis katmanını TERK ETMEZ
- Giriş: CreateXRequest / UpdateXRequest (Jakarta Validation)
- Çıkış: XSummaryResponse (liste) / XDetailResponse (detay)
- Arama: XSearchCriteria + JPA Specification
- Java record + MapStruct
- Liste ekranları → interface projection
- Rapor/KPI/AI → native query + projection
- Sayfalama: kendi PageResponse<T> (Spring Page'i dışa vermeme)

## 9. KARAR: API sözleşmesi ✅
- /api/v1/..., kaynak isimleri çoğul + kebab-case
- Sayfalama: ?page=0&size=20&sort=createdAt,desc
- Hata: RFC 9457 ProblemDetail + @RestControllerAdvice + traceId
- Idempotency-Key: rezervasyon ve ödeme oluşturmada
- OpenAPI → openapi-typescript / orval ile TS üretimi (İLK HAFTA)

## 10. KARAR: KPI tanımları (DONDURULDU) ✅
- Doluluk = kirada gün / hazır gün (hazır = takvim − bakım/hasar)
- Gelir = kira + ekstra + km aşım + geç teslim (KDV HARİÇ)
- Gelir tanıma: sözleşme tutarı GÜN GÜN dağıtılır
- Maliyet = bakım + yakıt + sigorta + MTV + hasar + ceza + amortisman
- Amortisman = (alış × 0,70) / (5 × 365) günlük, doğrusal
- Net Kâr = Gelir − Maliyet ; Günlük Katkı = Net Kâr / filoda gün
- Kısmi gün: saat bazlı oranlanır
- Teknik: vehicle_daily_fact tablosu + gece job'ı (canlı GROUP BY değil)

## 11. KARAR: AI mimarisi ✅
- Yaklaşım: TOOL CALLING (LLM hesaplamaz, backend hesaplar, LLM yorumlar)
- ⛔ Text-to-SQL YOK
- Tool'lar: getFleetSummary, getVehiclePerformance, getVehicleUsage,
  getRevenueBreakdown, getMaintenanceCosts, getAvailability,
  recommendVehicle, getReservationStats
- Güvenlik:
    * tenant + rol scope tool İÇİNDE, SecurityContext'ten (LLM'e güvenilmez)
    * SADECE OKUMA — yazma işlemi insan onayı gerektirir
    * PII maskeleme (isim/TCKN LLM'e gitmez, ID gider)
    * Her çağrı loglanır (soru, tool, token, süre, kullanıcı, maliyet)
    * Rate limiting (Bucket4j)
    * AI kapalıyken panel çalışmaya DEVAM eder (fallback)
- Sağlayıcı soyutlaması: kendi AiAssistantService arayüzüm
- RAG: sadece doküman soruları için, sonra (pgvector)

## 12. KARAR: Güvenlik ✅
- Access token ~15 dk + refresh token (DB'de, iptal edilebilir)
- Şifre: BCrypt/Argon2
- CORS: profil bazlı (dev: localhost:5173)
- KVKK: TCKN/ehliyet şifreli, loglara PII YAZILMAZ, saklama süresi politikası
- Sırlar: .env + environment variable (repoya ASLA)
- CurrentUserProvider arayüzü → Keycloak geçiş kapısı açık

## 13. Yol Haritası
- [ ] A0 Temel: ana sınıfı köke al, Flyway, BaseEntity, ProblemDetail,
  OpenAPI, Docker Compose, eski paketleri modüllere taşı
- [ ] A1 Kimlik: JWT + roller + company/branch/user  → frontend başlar
- [ ] A2 Filo: vehicle_model, vehicle, status_history, CRUD + arama
- [ ] A3 Müşteri + Rezervasyon: müsaitlik, exclusion constraint,
  durum makinesi, fiyat hesaplama  ← PROJENİN KALBİ, en çok test
- [ ] A4 Teslim/İade + Maliyetler: sözleşme, km/yakıt, hasar, bakım, gider
- [ ] A5 Analitik: vehicle_daily_fact job'ı + KPI endpoint'leri
- [ ] A6 AI: tool'lar → chat endpoint → streaming → guardrail + log
- [ ] A7 Abonelik: plan, kota, fatura, PAST_DUE otomasyonu

## 14. KARAR: Fiyatlama ✅
- Fiyat MODEL/SEGMENT bazlı (plaka bazlı DEĞİL)
- rate_plan (baz günlük) + rate_tier (süre kademesi) + season (çarpan) + extra
- Hesap: baz × kademe × sezon → × gün + ekstra + tek yön − indirim + KDV
- Kademeler: 1–3=1,00 | 4–7=0,90 | 8–29=0,80 | 30+=0,65
- Sezon: ölü=0,85 | normal=1,00 | yüksek=1,40 | bayram=1,60
- ⚠️ Rezervasyon onayında fiyat DONDURULUR (price_snapshot jsonb)

## 15. KARAR: Ödeme ✅
- PaymentProvider arayüzü: Manual (A3) → Iyzico/PayTR (A4) → Mock (test)
- ⛔ Kart bilgisi DB'de TUTULMAZ → sadece tokenizasyon (token + last4 + brand)
- Depozito: BLOKAJ tercih (kısa kiralama), TAHSİL-İADE yedek (uzun kiralama)
    * Blokaj: para çekilmez, limit bloke; void veya capture
    * auth_expires_at izlenir, süre dolmadan uyarı/yenileme job'ı
- Depozito tutarı segment bazlı (rate_plan.deposit_amount)
- payment tablosu: type/method/status + idempotency_key + parent_payment_id

## 16. KARAR: Aylık taksitli kiralama (onaylı tekrarlayan ödeme) ✅
- payment_schedule: rental_id, installment_no, period, amount, due_date
- Akış: PENDING → NOTIFIED (vade−3 gün) → APPROVED (müşteri linki)
  → CHARGED ; aksi EXPIRED / DECLINED / FAILED (max 3 retry)
- Onay linki: tek kullanımlık, 48 saat
- approved_at + approved_ip + metin versiyonu SAKLANIR (ispat)
- Idempotency: "rental-{id}-inst-{no}" → çift çekim imkânsız
- Personel manuel onay girebilir (APPROVED_BY_STAFF + not)

## 17. KARAR: Findeks / kredi skoru ✅
- Otomatik sorgu için KKB kurumsal üyelik + sözleşme ŞART (güncel şartlar teyit edilecek)
- Açık rıza ZORUNLU (SMS OTP / ıslak imza) → customer_consent tablosu
- CreditScoreProvider arayüzü: Manual (şimdi) → Findeks (A5+)
- SAKLANIR: skor bandı, tarih, referans no, iç risk kararı
- SAKLANMAZ: rapor dökümü, borç detayı, banka ilişkileri
- Risk bandı → depozito çarpanı + segment kısıtı (risk_policy tablosu, DB'den yönetilir)

## 18. KARAR: AI kotası ✅
- Birim: TOKEN (mesaj sayısı DEĞİL)
- Kullanıcı/gün: STAFF 30K | MANAGER 200K | OWNER 500K | ACCOUNTANT 100K | SUPER_ADMIN ∞
- Dakika limiti: 5 / 15 / 20 / 10 / 30 (Bucket4j)
- Şirket/ay: BRANCH_MONTHLY 2M | CORPORATE_ANNUAL 10M + şube başı 1M
- %80 uyarı, %100'de AI kapanır ama RAPORLAR ÇALIŞIR
- Maliyet düşürme: model routing + tool sonucu cache (5–15 dk) + bağlam budama
- ai_usage_log: token, model, tools_called, latency, cost, cached

## 19. Altyapı sınıfları
- BaseEntity (@MappedSuperclass) → common/entity/
  id, createdAt/By, updatedAt/By, deletedAt, @Version
  Tüm entity'ler extends eder. @Data KULLANMA (JPA equals tuzağı)
- JpaAuditingConfig → config/  (AuditorAware ile createdBy otomatik)
- TenantContext (ThreadLocal) → tenancy/
  ⚠️ finally { clear() } ASLA atlanmaz — thread havuzu veri sızdırır
- TenantFilter → security/filter/  (JWT → TenantContext)
- TenantIdentifierResolver → tenancy/  (Hibernate @TenantId köprüsü)
- CurrentUserProvider arayüzü → security/  (Keycloak geçiş kapısı)

## 20. KARAR: Fiyat yetkisi ✅
- Fiyat/indirim/sezon değerlerini GELİŞTİRİCİ belirlemez
- COMPANY_OWNER: tüm şubeler + sezon takvimi
- BRANCH_MANAGER: kendi şubesi
- BRANCH_STAFF: salt okuma
- Koddaki sayılar sadece yeni firma için DEFAULT seed


