package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Empleado;
import com.mycompany.myapp.domain.Venta;
import com.mycompany.myapp.repository.CocheRepository;
import com.mycompany.myapp.repository.EmpleadoRepository;
import com.mycompany.myapp.repository.VentaRepository;
import com.mycompany.myapp.service.VentaService;
import com.mycompany.myapp.service.dto.VentaDTO;
import com.mycompany.myapp.service.mapper.VentaMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Venta}.
 */
@Service
@Transactional
public class VentaServiceImpl implements VentaService {

    private final Logger log = LoggerFactory.getLogger(VentaServiceImpl.class);

    private final VentaRepository ventaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final CocheRepository cocheRepository;

    private final VentaMapper ventaMapper;

    public VentaServiceImpl(
        VentaRepository ventaRepository,
        VentaMapper ventaMapper,
        EmpleadoRepository empleadoRepository,
        CocheRepository cocheRepository
    ) {
        this.ventaRepository = ventaRepository;
        this.ventaMapper = ventaMapper;
        this.empleadoRepository = empleadoRepository;
        this.cocheRepository = cocheRepository;
    }

    // Esto nos devuelve un dto que como la base de datos necesita un entity
    // lo casteamos a entity y como tiene que devolver un dto lo volvemos
    // a castear a dto
    @Override
    public VentaDTO save(VentaDTO ventaDTO) {
        log.debug("Request to save Venta : {}", ventaDTO);
        Venta venta = ventaMapper.toEntity(ventaDTO);
        venta = ventaRepository.save(venta);
        if (null != venta.getEmpleado()) {
            // sumar uno al numero de ventas de un empleado
            Empleado empleado = venta.getEmpleado();
            if (null == empleado.getNumeroVentas()) {
                empleado.setNumeroVentas(0);
            }
            empleado.setNumeroVentas(empleado.getNumeroVentas() + 1);
            // se guarda en la base de datos la venta
            empleadoRepository.save(empleado);
        }
        return ventaMapper.toDto(venta);
    }

    public VentaDTO update(VentaDTO ventaDTO) {
        log.debug("Request to save Venta : {}", ventaDTO);
        Venta venta = ventaMapper.toEntity(ventaDTO);
        // Añadir y restar ventas
        Optional<Venta> va = ventaRepository.findById(venta.getId());
        if (va.isPresent()) {
            Venta ventaAnterior = va.get();
            Empleado empleadoVentaNueva = venta.getEmpleado();
            Empleado empleadoVentaAntigua = ventaAnterior.getEmpleado();

            if (empleadoVentaNueva.getId() != empleadoVentaAntigua.getId()) {
                empleadoVentaAntigua.setNumeroVentas(empleadoVentaAntigua.getNumeroVentas() - 1);
                empleadoVentaNueva.setNumeroVentas(empleadoVentaNueva.getNumeroVentas() + 1);
                empleadoRepository.save(empleadoVentaAntigua);
                empleadoRepository.save(empleadoVentaNueva);
            }
        }
        venta = ventaRepository.save(venta);
        return ventaMapper.toDto(venta);
    }

    @Override
    public Optional<VentaDTO> partialUpdate(VentaDTO ventaDTO) {
        log.debug("Request to partially update Venta : {}", ventaDTO);

        return ventaRepository
            .findById(ventaDTO.getId())
            .map(existingVenta -> {
                ventaMapper.partialUpdate(existingVenta, ventaDTO);

                return existingVenta;
            })
            .map(ventaRepository::save)
            .map(ventaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Ventas");
        return ventaRepository.findAll(pageable).map(ventaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VentaDTO> findOne(Long id) {
        log.debug("Request to get Venta : {}", id);
        return ventaRepository.findById(id).map(ventaMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Venta : {}", id);
        ventaRepository.deleteById(id);
    }
}
