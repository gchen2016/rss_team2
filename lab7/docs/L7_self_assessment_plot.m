close all;

lab = {'Programming','Hardware','Mechanics of Manipulation','Visual Navigation'};

% David
D1 = [4 3 2 3]; % before
D2 = [4 3 3.5 3]; % after

% Gabe
G1 = [4 1 2 2]; % before
G2 = [4 1 3 2]; % after

% Syler
S1 = [3 1.5 1 1.5]; % before
S2 = [3 1.5 1.5 1.5]; % after

% Vincent
V1 = [4 2 1 3]; % before
V2 = [4 2 2 3]; % after


% bar x-axis labels
n = length(lab);
x = 1:n; 


xRot = 35; % xTickLabelRotation

figure
subplot(2,2,1);

width1 = 0.5;
bar(x,D2,width1,'FaceColor','r')
hold on
bar(x,D1,width1,'FaceColor','b')
width2 = width1/2;
%  ylim([0, 4]);
 set(gca, 'XTickLabel',lab)
set(gca,'XTickLabelRotation',xRot)
set(gca,'ylim',[0 5])
set(gca,'xlim',[0.5 n+0.5])
% legend('Change','Before L3/4','location','southeastoutside') % add legend
title('David')

subplot(2,2,2);

width1 = 0.5;
bar(x,G2,width1,'FaceColor','r')
hold on
bar(x,G1,width1,'FaceColor','b')
width2 = width1/2;
%  ylim([0, 4]);
 set(gca, 'XTickLabel',lab)
set(gca,'XTickLabelRotation',xRot)
set(gca,'ylim',[0 5])
set(gca,'xlim',[0.5 n+0.5])
% legend('Change','Before L3/4','location','northeastoutside') % add legend
title('Gabe')

subplot(2,2,4);

width1 = 0.5;
bar(x,V2,width1,'FaceColor','r')
hold on
bar(x,V1,width1,'FaceColor','b')
width2 = width1/2;
%  ylim([0, 4]);
 set(gca, 'XTickLabel',lab)
set(gca,'XTickLabelRotation',xRot)
set(gca,'ylim',[0 5])
set(gca,'xlim',[0.5 n+0.5])
% legend('Change','Before L3/4','location','northeastoutside') % add legend
title('Vincent')

subplot(2,2,3);

width1 = 0.5;
bar(x,S2,width1,'FaceColor','r')
hold on
bar(x,S1,width1,'FaceColor','b')
width2 = width1/2;
%  ylim([0, 4]);
 set(gca, 'XTickLabel',lab)
set(gca,'XTickLabelRotation',xRot)
set(gca,'ylim',[0 5])
set(gca,'xlim',[0.5 n+0.5])
% legend('Change','Before L3/4','location','northeastoutside') % add legend
title('Syler')

    set(gcf, 'PaperPosition', [0 0 30 18]); %Position plot at left hand corner with width 5 and height 5.
    set(gcf, 'PaperSize', [30 18]); %Set the paper to have width 5 and height 5.
    saveas(gcf, './lab7__self_assessment.png')

