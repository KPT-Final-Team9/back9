package com.core.back9.mapper;

import com.core.back9.dto.ScoreDTO;
import com.core.back9.entity.Room;
import com.core.back9.entity.Score;
import com.core.back9.entity.constant.RatingType;
import org.mapstruct.*;

import java.time.YearMonth;
import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ScoreMapper {

    Score toEntity(ScoreDTO.UpdateRequest updateRequest);

    ScoreDTO.UpdateResponse toUpdateResponse(Score score);

    ScoreDTO.Info toInfo(Score score);

    @Mapping(source = "completed", target = "completed")
    ScoreDTO.InfoWithCompletionStatus toInfoWithCompletionStatus(Score score, boolean completed);

    @Mapping(source = "year", target = "selectedYear")
    @Mapping(source = "quarter", target = "selectedQuarter")
    @Mapping(target = "totalAvg", expression = "java(calculateTotalAvg(scores))")
    @Mapping(target = "facilityAvg", expression = "java(calculateScoreTypeAvg(scores, com.core.back9.entity.constant.RatingType.FACILITY))")
    @Mapping(target = "managementAvg", expression = "java(calculateScoreTypeAvg(scores, com.core.back9.entity.constant.RatingType.MANAGEMENT))")
    @Mapping(target = "complaintAvg", expression = "java(calculateScoreTypeAvg(scores, com.core.back9.entity.constant.RatingType.COMPLAINT))")
    ScoreDTO.AvgByQuarter toQuarterlyTotalAvg(int year, int quarter, List<Score> scores);

    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "room.name", target = "roomName")
    @Mapping(target = "totalAvg", expression = "java(calculateTotalAvg(scores))")
    ScoreDTO.TotalAvgByRoom toTotalAvgByRoom(Room room, List<Score> scores);

    ScoreDTO.CurrentAndBeforeQuarterlyTotalAvg toQuarterlyTotalAvgWithCurrentAndBefore(
            List<ScoreDTO.TotalAvgByRoom> current, List<ScoreDTO.TotalAvgByRoom> before
    );

    @Mapping(source = "current", target = "selectedMonth")
    @Mapping(target = "totalAvg", expression = "java(calculateTotalAvg(scores))")
    @Mapping(target = "evaluationProgress", expression = "java(calculateEvaluationProgress(scores))")
    @Mapping(target = "facilityAvg", expression = "java(calculateScoreTypeAvg(scores, com.core.back9.entity.constant.RatingType.FACILITY))")
    @Mapping(target = "managementAvg", expression = "java(calculateScoreTypeAvg(scores, com.core.back9.entity.constant.RatingType.MANAGEMENT))")
    @Mapping(target = "complaintAvg", expression = "java(calculateScoreTypeAvg(scores, com.core.back9.entity.constant.RatingType.COMPLAINT))")
    ScoreDTO.AllAvgByMonth toAllAvgWithMonth(YearMonth current, List<Score> scores);

    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "room.name", target = "roomName")
    ScoreDTO.AllAvgByRoom toAllAvgWithMonthByRoom(Room room, List<ScoreDTO.AllAvgByMonth> allAvgByMonthList);

    ScoreDTO.ListOfYearAvgWithMeAndOthers toListOfYearAvgWithMeAndOthers(
            List<ScoreDTO.AllAvgByMonth> my, List<ScoreDTO.AllAvgByMonth> others
    );

    @Named("calculateTotalAvg")
    default float calculateTotalAvg(List<Score> scores) {
        return (float) scores.stream()
                .filter(score -> score.getScore() >= 0)
                .mapToInt(Score::getScore).average().orElse(0);
    }

    @Named("calculateEvaluationProgress")
    default float calculateEvaluationProgress(List<Score> scores) {
        float completed = scores.stream().filter(score -> score.getScore() >= 0).count();
        return completed > 0 ? completed / scores.size() * 100 : 0;
    }

    @Named("calculateScoreTypeAvg")
    default float calculateScoreTypeAvg(List<Score> scores, RatingType ratingType) {
        return (float) scores.stream()
                .filter(score -> score.getRatingType() == ratingType)
                .mapToInt(Score::getScore)
                .average()
                .orElse(0);
    }

}
