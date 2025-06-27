package com.recnar.aws.s3.service.implement;

import com.recnar.aws.s3.db.entity.OrdenIngresoDetalle;
import com.recnar.aws.s3.db.repository.IOrdenIngresoDetalleRepository;
import com.recnar.aws.s3.dto.CartaCertificadosRequest;
import com.recnar.aws.s3.dto.ReportRequest;
import com.recnar.aws.s3.service.IReportServices;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ReportServicesImpl implements IReportServices {

    private final ResourceLoader resourceLoader;

    @Autowired
    private IOrdenIngresoDetalleRepository iOrdenIngresoDetalleRepository;

    public ReportServicesImpl(ResourceLoader resourceLoader, DataSource dataSource) {
        this.resourceLoader = resourceLoader;
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    @Override
    public String generarCertificacionFinal(int idDetalleOrdenIngreso) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();

            Optional<OrdenIngresoDetalle> resp = iOrdenIngresoDetalleRepository.findById((long) idDetalleOrdenIngreso);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("id", idDetalleOrdenIngreso);
            Resource resource = null;


            if (resp.get().getIdCategoria() != 1) {
                if (resp.get().getAprobado().equals("S")) {
                    resource = resourceLoader.getResource("classpath:reports/CertificacionFinalFirmado.jasper");

                } else {
                    resource = resourceLoader.getResource("classpath:reports/CertificacionFinalSinFirmar.jasper");
                }
            } else {

                if (resp.get().getAprobado().equals("S")) {
                    resource = resourceLoader.getResource("classpath:reports/CertificacionFinalFirmadoRym.jasper");

                } else {
                    resource = resourceLoader.getResource("classpath:reports/CertificacionFinalSinFirmarRym.jasper");
                }
            }

            InputStream jasperStream = resource.getInputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);


            byte[] pdfContent = JasperExportManager.exportReportToPdf(jasperPrint);
            return Base64.getEncoder().encodeToString(pdfContent);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String genererarRemision(String codigoSolicitud) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("codigo_solicitud",codigoSolicitud);
            Resource resource = resourceLoader.getResource("classpath:reports/Remision_despachos.jasper");
            InputStream jasperStream = resource.getInputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);


            byte[] pdfContent = JasperExportManager.exportReportToPdf(jasperPrint);
            return Base64.getEncoder().encodeToString(pdfContent);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    }

    @Override
    public String generarCartaCertificado(CartaCertificadosRequest cartaCertificadosRequest) {

        ReportRequest request = new ReportRequest();
        request.setNombreReporte("carta_certificacion");
        Map<String, Object> params = new HashMap<>();
        params.put("nombre", cartaCertificadosRequest.getNombre());
        params.put("identificacion", cartaCertificadosRequest.getIdentificacion());
        params.put("codigo_certificado", cartaCertificadosRequest.getCodigoCertificado());
        params.put("anio", cartaCertificadosRequest.getAnio());
        params.put("fecha", cartaCertificadosRequest.getFecha());
        request.setParameters(params);

        return generarReporte(request);

    }



    @Override
    public String generarReporte(ReportRequest reportRequest) {
        Connection connection = null;
        String path="classpath:reports/"+reportRequest.getNombreReporte()+".jasper";

        try {
            connection = dataSource.getConnection();
            Map<String, Object> parameters = reportRequest.getParameters();
            Resource resource = resourceLoader.getResource(path);
            InputStream jasperStream = resource.getInputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);


            byte[] pdfContent = JasperExportManager.exportReportToPdf(jasperPrint);
            return Base64.getEncoder().encodeToString(pdfContent);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
