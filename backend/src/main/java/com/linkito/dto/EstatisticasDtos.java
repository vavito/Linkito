package com.linkito.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EstatisticasDtos {

    public static class RespostaDashboard {
        public long totalLinks;
        public long totalCliques;
        public LinkDtos.RespostaLink linkMaisAcessado;
        public List<LinkDtos.RespostaLink> ultimosLinks;
    }

    public static class RespostaEstatisticasLink {
        public UUID linkId;
        public String codigoCurto;
        public long totalCliques;
        public List<RespostaClique> cliques;
    }

    public static class RespostaClique {
        public UUID id;
        public String enderecoIp;
        public String agenteUsuario;
        public LocalDateTime clicadoEm;
    }
}
