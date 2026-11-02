package tech.squadmc.squadmcor.smoke;

/**
 * Перенесено из aasgranate (com.danilfb123.aasgranate.api.SmokeCloudEmitter).
 * Логика и сигнатуры 1:1, переименован только пакет. Зависимости от
 * исходного мода нет — squadmc полностью автономен.
 *
 * Общий контракт для любой сущности, у которой есть объёмное дымовое
 * облако. VehicleSmokeStageRenderer рендерит ЛЮБУЮ сущность, реализующую
 * этот интерфейс, — это точка расширения для техники.
 */
public interface VehicleSmokeEmitter {

    /** Идёт ли сейчас дым (облако раскрывается / держится / затухает). */
    boolean isSmoking();

    /**
     * 0..1 — насколько плотное облако прямо сейчас (растёт GROW, держится 1.0
     * на HOLD, падает до 0 на FADE). partialTick — для интерполяции между тиками.
     */
    float getSmokeDensity(float partialTick);

    /** 0..1 — насколько облако раскрылось по объёму (используется для радиуса/высоты). */
    float getSmokeGrowth(float partialTick);

    /** Сырой счётчик тиков дымления (для анимации шума в шейдере/пуфах), или -1, если дыма нет. */
    float getSmokeTicks(float partialTick);

    /** Целевой (максимальный) радиус облака в блоках. */
    float getSmokeRadius();

    /** Целевая (максимальная) высота облака в блоках. */
    float getSmokeHeight();

    /** 0..1: множитель числа клубов и прозрачности. */
    default float getSmokeIntensity() { return 1.0F; }
}
