package com.springbatch.demonstrativoorcamentario.writer;

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.springbatch.demonstrativoorcamentario.dominio.GrupoLancamento;
import com.springbatch.demonstrativoorcamentario.dominio.Lancamento;
import org.springframework.core.io.Resource;

@Configuration
public class DemonstrativoOrcamentarioWriterConfig {

    @StepScope
  @Bean
	public FlatFileItemWriter<GrupoLancamento> demonstrativoOrcamentarioWriter(
          @Value("#{jobParameters['demostrativoOrcamentario']}") Resource resource,
          DemostrativoOrçamentarioRodape rodapeCallback

  ){

      return  new FlatFileItemWriterBuilder<GrupoLancamento>()
              .name("demonstrativoOrcamentarioWriter")
              .resource(resource)
              .lineAggregator(lineAggregator())
              .headerCallback(cabecalhoCallback())
              .footerCallback(rodapeCallback)
              .build();
  }

    private FlatFileHeaderCallback cabecalhoCallback() {
        return new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.append(String.format("SISTEMA INTEGRADO: XPTO \t\t\t\t DATA: %s\n", new SimpleDateFormat("dd/MM/yyyy").format(new Date())));
                writer.append(String.format("MÓDULO: ORÇAMENTO \t\t\t\t\t HORA: %s\n", new SimpleDateFormat("HH:MM").format(new Date())));
                writer.append(String.format("\t\t\tDEMONSTRATIVO ORCAMENTARIO\n"));
                writer.append(String.format("----------------------------------------------------------------------------\n"));
                writer.append(String.format("CODIGO NOME VALOR\n"));
                writer.append(String.format("\t Data Descricao Valor\n"));
                writer.append(String.format("----------------------------------------------------------------------------\n"));
            }
        } ;
        }

    private LineAggregator<GrupoLancamento> lineAggregator() {
       return new LineAggregator<GrupoLancamento>() {
           @Override
           public String aggregate(GrupoLancamento grupo) {
               String formatoGruppoLancamento = String.format("[%d] %s - %s\n", grupo.getCodigoNaturezaDespesa(),
                       grupo.getDescricaoNaturezaDespesa(),
                       NumberFormat.getCurrencyInstance().format(grupo.getTotal()));

               StringBuilder stringBuilder = new StringBuilder();
               for (Lancamento lancamento : grupo.getLancamentos()) {
                   stringBuilder.append(String.format("\t [%s] %s - %s\n", new SimpleDateFormat("dd/MM/yyyy").format(lancamento.getData()), lancamento.getDescricao(),
                           NumberFormat.getCurrencyInstance().format(lancamento.getValor())));
               }
                String formatLancamento = stringBuilder.toString();
               return formatoGruppoLancamento + formatLancamento;
           }
       };
    }

}

