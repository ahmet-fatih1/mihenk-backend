package com.rentacar.tenancy;

public final class TenantContext {

    public record TenantInfo(Long companyId, Long branchId, Long userId, boolean superAdmin) {}

    private static final ThreadLocal<TenantInfo> HOLDER = new ThreadLocal<>();

    public static void set(TenantInfo info) { HOLDER.set(info); }

    public static TenantInfo get() {
        TenantInfo info = HOLDER.get();
        if (info == null) throw new IllegalStateException("Tenant bağlamı yok");
        return info;
    }

    public static Long companyId()  { return get().companyId(); }
    public static Long branchId()   { return get().branchId(); }
    public static boolean isSuperAdmin() { return get().superAdmin(); }

    public static void clear() { HOLDER.remove(); }   // ⚠️ HAYATİ

    private TenantContext() {}
}

