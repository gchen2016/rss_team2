
close all;

D1 = [3 4 4]; % before
D2 = [4 4 5]; % after

G1 = [1 1 3];
G2 = [1 1 3];

S1 = [1.5 4 2];
S2 = [1.5 4 2];

V1 = [3 2 2];
V2 = [3 2 3];

% bar x-axis labels
lab = {'Electronics','Data Analysis','Motion Control'};
x=1:length(lab)


figure
subplot(2,2,1);

width1 = 0.5;
bar(x,D2,width1,'FaceColor','r')
hold on
bar(x,D1,width1,'FaceColor','b')
width2 = width1/2;
%  ylim([0, 4]);
 set(gca, 'XTickLabel',lab)
set(gca,'XTickLabelRotation',50)
set(gca,'ylim',[0 5])
set(gca,'xlim',[0.5 3.5])
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
set(gca,'XTickLabelRotation',50)
set(gca,'ylim',[0 5])
set(gca,'xlim',[0.5 3.5])
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
set(gca,'XTickLabelRotation',50)
set(gca,'ylim',[0 5])
set(gca,'xlim',[0.5 3.5])
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
set(gca,'XTickLabelRotation',50)
set(gca,'ylim',[0 5])
set(gca,'xlim',[0.5 3.5])
% legend('Change','Before L3/4','location','northeastoutside') % add legend

title('Syler')

    set(gcf, 'PaperPosition', [0 0 30 18]); %Position plot at left hand corner with width 5 and height 5.
    set(gcf, 'PaperSize', [30 18]); %Set the paper to have width 5 and height 5.
    saveas(gcf, './L5_self_assessment.png')
