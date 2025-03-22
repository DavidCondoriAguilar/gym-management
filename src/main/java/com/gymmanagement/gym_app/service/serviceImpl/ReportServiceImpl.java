package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.model.ReportModel;
import com.gymmanagement.gym_app.repository.GymMemberRepository;
import com.gymmanagement.gym_app.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final GymMemberRepository gymMemberRepository;

    @Override
    public ReportModel getRetentionReport() {
        List<GymMember> members = gymMemberRepository.findAll();

        long totalMembers = members.size();

        // Renovados: suponemos que un miembro renovado tiene más de 1 registro en membershipRecords
        long renewedMembers = members.stream()
                .filter(m -> m.getMembershipRecords() != null && m.getMembershipRecords().size() > 1)
                .count();

        // Nuevos: miembros registrados en los últimos 30 días
        long newMembers = members.stream()
                .filter(m -> m.getRegistrationDate().isAfter(LocalDate.now().minusDays(30)))
                .count();

        // Churn: miembros inactivos (active==false)
        long churnedMembers = members.stream()
                .filter(m -> Boolean.FALSE.equals(m.getActive()))
                .count();

        long activeMembers = members.stream()
                .filter(m -> Boolean.TRUE.equals(m.getActive()))
                .count();

        long inactiveMembers = churnedMembers; // Usamos el mismo valor de inactivos

        double retentionRate = totalMembers > 0 ? ((double) renewedMembers / totalMembers) * 100 : 0;

        // Duración promedio de la membresía (en meses)
        double averageDuration = members.stream()
                .mapToLong(m -> ChronoUnit.MONTHS.between(m.getMembershipStartDate(), m.getMembershipEndDate()))
                .average().orElse(0);

        // Ingresos del mes y cálculo de descuentos:
        LocalDate now = LocalDate.now();

        // Inicializamos variables para acumular ingresos y descuentos
        BigDecimal membershipRevenueThisMonth = BigDecimal.ZERO;
        BigDecimal totalDiscountsApplied = BigDecimal.ZERO;

        // Filtramos miembros que iniciaron su membresía en el mes actual
        List<GymMember> membersThisMonth = members.stream()
                .filter(m -> m.getMembershipStartDate().getMonth() == now.getMonth() &&
                        m.getMembershipStartDate().getYear() == now.getYear())
                .collect(Collectors.toList());

        for (GymMember member : membersThisMonth) {
            BigDecimal originalCost = member.getMembershipPlan().getCost();
            BigDecimal effectiveCost = originalCost;
            BigDecimal discountAmount = BigDecimal.ZERO;

            // Si tiene promociones, se aplica el mejor descuento (mayor porcentaje)
            if (member.getPromotions() != null && !member.getPromotions().isEmpty()) {
                BigDecimal maxDiscountPercentage = member.getPromotions().stream()
                        .map(Promotion::getDiscountPercentage)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                discountAmount = originalCost.multiply(maxDiscountPercentage)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                effectiveCost = originalCost.subtract(discountAmount);
            }
            membershipRevenueThisMonth = membershipRevenueThisMonth.add(originalCost);
            totalDiscountsApplied = totalDiscountsApplied.add(discountAmount);
        }

        // Ingresos netos: ingresos totales menos descuentos aplicados
        BigDecimal netMembershipRevenueThisMonth = membershipRevenueThisMonth.subtract(totalDiscountsApplied)
                .setScale(2, RoundingMode.HALF_UP);

        membershipRevenueThisMonth = membershipRevenueThisMonth.setScale(2, RoundingMode.HALF_UP);
        totalDiscountsApplied = totalDiscountsApplied.setScale(2, RoundingMode.HALF_UP);

        // Plan de membresía más popular: el que tiene mayor cantidad de suscriptores
        Map<String, Long> planCounts = members.stream()
                .collect(Collectors.groupingBy(m -> m.getMembershipPlan().getName(), Collectors.counting()));

        String mostPopularMembershipPlan = planCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        return new ReportModel(
                totalMembers,
                renewedMembers,
                newMembers,
                churnedMembers,
                retentionRate,
                averageDuration,
                activeMembers,
                inactiveMembers,
                membershipRevenueThisMonth,
                mostPopularMembershipPlan,
                totalDiscountsApplied,
                netMembershipRevenueThisMonth
        );
    }
}
