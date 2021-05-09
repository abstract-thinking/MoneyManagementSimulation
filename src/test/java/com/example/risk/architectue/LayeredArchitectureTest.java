package com.example.risk.architectue;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Disabled;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@Disabled
@AnalyzeClasses(packages = "com.example.risk")
public class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule layer_dependencies_are_respected = layeredArchitecture()
            .layer("Boundaries").definedBy("com.example.risk.boundary..")
            .layer("Controllers").definedBy("com.example.risk.control..")
            .layer("Converters").definedBy("com.example.risk.converter..")
            .layer("Persistence").definedBy("com.example.risk.data..")
            .layer("Services").definedBy("com.example.risk.service..")

            .whereLayer("Boundaries").mayNotBeAccessedByAnyLayer()
            .whereLayer("Controllers").mayOnlyBeAccessedByLayers("Boundaries")
            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Controllers")
            .whereLayer("Converters").mayOnlyBeAccessedByLayers("Controllers")
            .whereLayer("Services").mayOnlyBeAccessedByLayers("Converters");
}
