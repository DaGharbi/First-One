import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Home } from './home/home';
import { Administratif } from './administratif/administratif';
import { Menu } from './menu/menu';
import { GestionUtilisateurs } from './administratif/gestion-utilisateurs/gestion-utilisateurs';
import { GestionAgences } from './administratif/gestion-agences/gestion-agences';
import { GestionZones } from './administratif/gestion-zones/gestion-zones';
import { AuthGuard } from './guards/auth.guard';
import { PreparationEnvironnement } from './pages/preparation-environnement/preparation-environnement';
import { TtfCommande } from './pages/transactions-transport/ttf-commande';
import { TtfVersement } from './pages/transactions-transport/ttf-versement';
import { TtfCommandesMonnaies } from './pages/transactions-transport/ttf-commandes-monnaies';
import { TtfAgentCommandesVersements } from './pages/transactions-transport/ttf-agent-commandes-versements';
import { TtfAgentCommandesMonnaies } from './pages/transactions-transport/ttf-agent-commandes-monnaies';
import { CalculEncaisseGenerationLimites } from './pages/calcul-encaisse/calcul-encaisse-generation-limites';
import { CalculEncaisseConsultationLimites } from './pages/calcul-encaisse/calcul-encaisse-consultation-limites';
import { CalculEncaisseConsultationLimitesDevise } from './pages/calcul-encaisse/calcul-encaisse-consultation-limites-devise';
import { StatistiquesReportPage } from './pages/statistiques/statistiques-report';
import { ConsultationPage } from './pages/consultation/consultation';

export const routes: Routes = [
  { path: 'login', component: Login },
  {
    path: 'home',
    component: Home,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'administratif',
        component: Administratif,
        canActivate: [AuthGuard],
        data: { roles: ['ADMIN', 'SECURITY'] },
        children: [
          { path: 'utilisateurs', component: GestionUtilisateurs },
          { path: 'agences', component: GestionAgences },
          { path: 'zones', component: GestionZones },
          { path: '', redirectTo: 'utilisateurs', pathMatch: 'full' }
        ]
      },
      { path: 'preparation-environnement', component: PreparationEnvironnement, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'CC'] } },
      { path: 'preparation-environnement/interrogation-soldes-caisses', component: PreparationEnvironnement, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'CC'] } },
      { path: 'preparation-environnement/calcul-commande-versement', component: PreparationEnvironnement, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'CC'] } },
      {
        path: 'transactions-transport',
        canActivate: [AuthGuard],
        data: { roles: ['ADMIN', 'CC', 'AGENT'] },
        children: [
          { path: 'commandes-versements', component: TtfCommande },
          { path: 'commande', component: TtfCommande },
          { path: 'versement', component: TtfCommande },
          { path: 'commande-versement-devises', component: TtfCommande },
          { path: 'commandes-monnaies', component: TtfCommandesMonnaies, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'CC'] } },
          { path: 'agent-commandes-versements', component: TtfAgentCommandesVersements, canActivate: [AuthGuard], data: { roles: ['AGENT'] } },
          { path: 'agent-commandes-monnaies', component: TtfAgentCommandesMonnaies, canActivate: [AuthGuard], data: { roles: ['AGENT'] } },
          { path: 'saisie-commande-monnaies', component: TtfCommandesMonnaies, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'CC'] } },
          { path: 'modification-commande-monnaies', component: TtfCommandesMonnaies, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'CC'] } },
          { path: 'validation-commandes-versement', component: TtfVersement },
          { path: 'commande-versement-devises-euro-dollar', component: TtfCommande },
          { path: 'commande-versement-euro-dollar', component: TtfCommande },
          { path: '', redirectTo: 'commandes-versements', pathMatch: 'full' }
        ]
      },
      {
        path: 'calcul-encaisse',
        canActivate: [AuthGuard],
        data: { roles: ['ADMIN', 'CC', 'AGENT'] },
        children: [
          { path: 'generation-limites', component: CalculEncaisseGenerationLimites },
          { path: 'consultation-limites', component: CalculEncaisseConsultationLimites },
          { path: 'consultation-limites-devise', component: CalculEncaisseConsultationLimitesDevise },
          { path: '', redirectTo: 'generation-limites', pathMatch: 'full' }
        ]
      },
      {
        path: 'statistiques',
        canActivate: [AuthGuard],
        data: { roles: ['ADMIN', 'CC', 'SECURITY'] },
        children: [
          {
            path: 'etat-passages',
            component: StatistiquesReportPage,
            data: {
              title: 'Etat des passages',
              lead: 'Suivi des passages traites, filtres par agence et synthese des volumes.'
            }
          },
          {
            path: 'etat-encaisses-moyennes',
            component: StatistiquesReportPage,
            data: {
              title: 'Etat des encaisses moyennes',
              lead: 'Visualisation des encaisses moyennes par periode, agence ou zone.'
            }
          },
          {
            path: 'etat-suivi-reservations-liquidites',
            component: StatistiquesReportPage,
            data: {
              title: 'Etat de suivi des reservations de liquidites',
              lead: 'Tableau de suivi des demandes, validations et consommations de liquidites.'
            }
          },
          {
            path: 'oisives-prevision-reservation-fonds',
            component: StatistiquesReportPage,
            data: {
              title: 'Encaisses oisives avec prevision de reservation de fonds',
              lead: 'Analyse des encaisses oisives accompagnees des previsions de reservation de fonds.'
            }
          },
          {
            path: 'oisives-collecte-fonds',
            component: StatistiquesReportPage,
            data: {
              title: 'Encaisses oisives avec collecte de fonds',
              lead: 'Suivi des encaisses oisives et des operations de collecte associees.'
            }
          },
          {
            path: 'volume-devises-achat-vente',
            component: StatistiquesReportPage,
            data: {
              title: 'Achat et vente des devises',
              lead: 'Reporting sur les volumes d achat et de vente des devises par periode.'
            }
          },
          {
            path: 'volume-devises-vente-versement-bct',
            component: StatistiquesReportPage,
            data: {
              title: 'Vente / versement BCT en devises',
              lead: 'Suivi des ventes et versements BCT en devises avec filtres de controle.'
            }
          },
          { path: '', redirectTo: 'etat-passages', pathMatch: 'full' }
        ]
      },
      { path: 'consultation', component: ConsultationPage, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'CC', 'AGENT'] } },
      { path: 'consultation/consultation-convoyeurs', component: ConsultationPage, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'CC', 'AGENT'] } },
      { path: 'consultation/passage-prestataires', component: ConsultationPage, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'CC', 'AGENT'] } },
      { path: 'menu', component: Menu, canActivate: [AuthGuard], data: { roles: ['ADMIN', 'CC', 'AGENT', 'SECURITY'] } },
      { path: '', redirectTo: 'menu', pathMatch: 'full' }
    ]
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];
