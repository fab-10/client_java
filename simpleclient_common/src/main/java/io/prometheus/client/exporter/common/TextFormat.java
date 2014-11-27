package io.prometheus.client.exporter.common;

import io.prometheus.client.Collector;

import java.util.Collections;
import java.util.Enumeration;
import java.io.IOException;
import java.io.Writer;

public class TextFormat {
  /**
   * Write out the text version 0.0.4 of the given MetricFamilySamples.
   */
  public static void write004(Writer writer, Enumeration<Collector.MetricFamilySamples> mfs) throws IOException {
    /* See https://docs.google.com/a/boxever.com/document/d/1ZjyKiKxZV83VI9ZKAXRGKaUKK2BIWCT7oiGBKDBpjEY/edit# 
     * for the output format specification. */
    for (Collector.MetricFamilySamples metricFamilySamples: Collections.list(mfs)) {
      writer.write("# HELP " + metricFamilySamples.name + " " + escapeHelp(metricFamilySamples.help) + "\n");
      writer.write("# TYPE " + metricFamilySamples.name + " " + typeString(metricFamilySamples.type) + "\n");
      for (Collector.MetricFamilySamples.Sample sample: metricFamilySamples.samples) {
        writer.write(sample.name);
        if (sample.labelNames.length > 0) {
          writer.write("{");
          for (int i = 0; i < sample.labelNames.length; ++i) {
            writer.write(sample.labelNames[i] + "=\"" + escapeLabelValue(sample.labelValues.get(i)) + "\",");
          }
          writer.write("}");
        }
        writer.write(" " + sample.value + "\n");
      }
    }
  }

  /**
   * Content-type for text verision 0.0.4.
   */
  public final static String CONTENT_TYPE_004 = "text/plain; version=0.0.4; charset=utf-8";

  static String escapeHelp(String s) {
    return s.replace("\\", "\\\\").replace("\n", "\\n");
  }
  static String escapeLabelValue(String s) {
    return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
  }

  static String typeString(Collector.Type t) {
    switch (t) {
      case GAUGE:
        return "gauge";
      case COUNTER:
        return "counter";
      case SUMMARY:
        return "summary";
      default:
        return "untyped";
    }
  }
}
