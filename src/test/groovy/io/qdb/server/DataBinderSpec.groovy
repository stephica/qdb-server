package io.qdb.server

import io.qdb.server.controller.JsonService
import io.qdb.server.databind.DataBinder
import io.qdb.server.databind.DataBindingException
import spock.lang.Specification

import java.text.SimpleDateFormat

class DataBinderSpec extends Specification {

    DataBinder b = new DataBinder(new JsonService(true))

    @SuppressWarnings("GroovyPointlessBoolean")
    def "Basic types work"() {
        TypesDTO dto = new TypesDTO()
        b.bind([
                boolValue: "true",
                boolWrapperValue: "false",
                intValue: "1",
                intWrapperValue: "2",
                longValue: "3",
                longWrapperValue: "4",
                stringValue: "abc",
                dateValue: "2013-06-16",
                stringArrayValue: "abc,def"
            ], dto);

        expect:
        dto.boolValue == true
        dto.boolWrapperValue == false
        dto.intValue == 1
        dto.intWrapperValue == 2
        dto.longValue == 3L
        dto.longWrapperValue == 4L
        dto.stringValue == "abc"
        dto.dateValue == new SimpleDateFormat("yyyy-MM-dd").parse("2013-06-16")
        dto.stringArrayValue == ["abc", "def"] as String[]
    }

    def "String arrays work"() {
        TypesDTO dto = new TypesDTO()
        b.bind([stringArrayValue: '["abc","def"]'], dto);
        TypesDTO dto2 = new TypesDTO()
        b.bind([stringArrayValue: ''], dto2);

        expect:
        dto.stringArrayValue == ["abc", "def"] as String[]
        dto2.stringArrayValue == [] as String[]
    }

    def "Unknown field error trapped"() {
        when:
        TypesDTO dto = new TypesDTO()
        b.bind([piggy: "oinks"], dto).check();

        then:
        DataBindingException e = thrown()
        e.errors.containsKey("piggy")
    }

    def "Unknown field error ignored"() {
        TypesDTO dto = new TypesDTO()
        b.ignoreInvalidFields(true).bind([intValue: 42, piggy: "oinks"], dto).check();

        expect:
        dto.intValue == 42
    }

    def "Type conversion error trapped"() {
        when:
        TypesDTO dto = new TypesDTO()
        b.bind([intValue: "oinks"], dto).check();

        then:
        DataBindingException e = thrown()
        e.errors.containsKey("intValue")
    }

    def "Params map works"() {
        TypesDTOWithParams dto = new TypesDTOWithParams()
        b.bind([intValue: 3, piggy: "oinks"], dto)

        expect:
        dto.intValue == 3
        dto.params == [piggy: "oinks"]
    }

    def "Number suffixes work"() {
        TypesDTO dto = new TypesDTO()
        b.bind([intValue: "1k", intWrapperValue: "1m", longValue: "1g"], dto).check();
        TypesDTO dto2 = new TypesDTO()
        b.bind([intValue: "1K", intWrapperValue: "1M", longValue: "1G"], dto2).check();

        expect:
        dto.intValue == 1024
        dto.intWrapperValue == 1024 * 1024
        dto.longValue == 1024 * 1024 * 1024L
        dto2.intValue == 1024
        dto2.intWrapperValue == 1024 * 1024
        dto2.longValue == 1024 * 1024 * 1024L
    }

    def "Converted values go back in map"() {
        TypesDTO dto = new TypesDTO()
        def map = [intValue: "1k"]
        b.updateMap(true).bind(map, dto).check();

        expect:
        dto.intValue == 1024
        map.intValue == 1024
    }

    def "Type mismatch trapped"() {
        when:
        TypesDTO dto = new TypesDTO()
        b.bind([intValue: 42L], dto).check();

        then:
        DataBindingException e = thrown()
        e.errors.containsKey("intValue")
        e.message.contains("intValue")
    }

}